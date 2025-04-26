const urlAdmin = '/api';

async function getRoles() {
    return await fetch("/api/roles")
        .then(response => response.json());
}

function listRoles() {
    let tmp = '';
    getRoles().then(roles => {
        const uniqueRoles = new Set();

        roles.forEach(role => {
            const roleName = role.roleName.substring(5);
            if (!uniqueRoles.has(roleName)) {
                uniqueRoles.add(roleName);
                tmp += `<option value="${role.id}">${roleName}</option>`;
            }
        });
    }).then(() => {
        console.log('listRoles');
        document.getElementById('editRoles').innerHTML = tmp;
        document.getElementById('deleteRoles').innerHTML = tmp;
        document.getElementById('rolesNewUser').innerHTML = tmp;
    });
}
listRoles();

function getUsersData() {
    fetch(urlAdmin + '/users')
        .then(response => response.json())
        .then(data => loadTable(data))
}

function loadTable(listUsers) {
    let el = "";
    for (let user of listUsers) {
        el += `
        <tr>
          <td>${user.id}</td>
          <td>${user.username}</td>
          <td>${user.lastName}</td>
          <td>${user.age}</td>
          <td>${user.email}</td>
          <td>${user.roles ? user.roles.map(role => role.roleName.substring(5)).join(', ') : 'No roles'}</td>
          <td class="align-middle">
			 <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#userEdit" onclick="editForm(${user.id})">
				Edit
		     </button>
		  </td>
		  <td class="align-middle">
			  <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#userDelete" onclick="deleteForm(${user.id})">
				 Delete
			  </button>
		  </td>
        </tr>`
    }
    document.getElementById("allUsers").innerHTML = el;
}

getUsersData();

document.getElementById("newUserForm").addEventListener('submit', (e) => {
    e.preventDefault();
    let role = document.getElementById("rolesNewUser");
    let rolesNewUser = [];
    for (let i = 0; i < role.options.length; i++) {
        if (role.options[i].selected) {
            rolesNewUser.push({id: role.options[i].value, roleName: "ROLE_" + role.options[i].innerHTML});
        }
    }

    fetch('api/users', {
        method: "POST",
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
                username: document.getElementById("first_name").value,
                lastName: document.getElementById("last_name").value,
                age: document.getElementById("age").value,
                email: document.getElementById("email").value,
                password: document.getElementById("password").value,
                roles: rolesNewUser
            }
        )
    }).then((response) => {
        if (response.ok) {
            getUsersData();
            document.getElementById('newUserForm').reset();
            document.getElementById("nav-home-tab").click();
        }
    })
})

function editForm(id) {
    fetch(urlAdmin + "/users/" + id, {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json;charset=UTF-8'
        }
    }).then(response => {
        response.json().then(async user => {
            document.getElementById("editId").value = user.id;
            document.getElementById("editFirstName").value = user.username;
            document.getElementById("editLastName").value = user.lastName;
            document.getElementById("editAge").value = user.age;
            document.getElementById("editEmail").value = user.email;
            const allRoles = await getRoles(); // Получаем все роли
            const rolesSelect = document.getElementById('editRoles');
            rolesSelect.innerHTML = ''; // Очищаем список ролей
            allRoles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.textContent = role.roleName.substring(5);
                option.selected = user.roles && user.roles.some(userRole => userRole.id === role.id);
                rolesSelect.appendChild(option);
            });
        });
    });
}

async function editUser() {
    const rolesSelect = document.getElementById('editRoles');
    let idValue = document.getElementById("editId").value;
    let nameValue = document.getElementById('editFirstName').value;
    let surnameValue = document.getElementById('editLastName').value;
    let ageValue = document.getElementById('editAge').value;
    let emailValue = document.getElementById('editEmail').value;
    let listOfRole = [];
    for (let i = 0; i < rolesSelect.options.length; i++) {
        if (rolesSelect.options[i].selected) {
            listOfRole.push({id: rolesSelect.options[i].value, roleName: "ROLE_" + rolesSelect.options[i].innerHTML});
        }
    }
    let user = {
        id: idValue,
        username: nameValue,
        lastName: surnameValue,
        age: ageValue,
        email: emailValue,
        password: document.getElementById("editPassword").value,
        roles: listOfRole
    };
    await fetch(urlAdmin + '/users', {
        method: "PUT",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(user)
    });
    closeModal();
    getUsersData();
}


document.getElementById("editForm").addEventListener('submit', (e) => {
    e.preventDefault();

})

function deleteForm(id) {
    fetch(urlAdmin + "/users/" + id).then(response => {
        response.json().then(user => {
            document.getElementById("deleteId").value = user.id;
            document.getElementById("deleteFirstName").value = user.username;
            document.getElementById("deleteLastName").value = user.lastName;
            document.getElementById("deleteAge").value = user.age;
            document.getElementById("deleteEmail").value = user.email;
            const roleDelete = document.getElementById("deleteRoles");
            roleDelete.innerHTML = ``;
            user.roles.forEach(role => {
                const option = document.createElement("option");
                option.textContent = role.roleName.substring(5);
                roleDelete.appendChild(option);
            })
        })
    })
}

async function deleteUser() {
    const id = document.getElementById("deleteId").value;
    let deleteMethod = {
        method: "DELETE", headers: {'Content-Type': 'application/json;charset=utf-8'}
    };
    fetch(urlAdmin + "/users/" + id, deleteMethod).then(() => {
        closeModal();
        getUsersData();
    });
};

function closeModal() {
    document.querySelectorAll(".btn-close").forEach((btn) => btn.click());

}


const validate = async () => {
    const users = await fetch("/api/users");
    const btnAdd = document.getElementById("btnAdd");
    const errorMessageEmail = document.getElementById("errorMessageEmail");
    const emails= users.map(user => user.email);
    const input = document.getElementById(inputEmail);
    let isContains = emails.includes(input.value);

    if (isContains) {
        if (btnAdd) {
            btnAdd.disabled = true;
            errorMessageEmail.style.display = "inline"
        }
    } else {
        btnAdd.disabled = false;
        errorMessageEmail.style.display = "none"
    }

}

document.getElementById("inputEmail").addEventListener('onchange', validate);