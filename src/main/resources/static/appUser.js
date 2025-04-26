const userInfoElement = document.querySelector('#usernameAuth')
const userRolesInfoElement = document.querySelector('#userAuthRoles')
const userTableElement = document.querySelector('#userInfoTable')
const apiUrl = '/api/user'

async function fetchCurretUser() {
    try {
        const response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`Error: ${response.status} ${response.statusText}`)
        }

        const user = await response.json();

        userInfoElement.textContent = user.username;
        userRolesInfoElement.textContent = user.roles
            ? user.roles.map(role => role.roleName.substring(5)).join(', ') : 'No roles';

        userTableElement.innerHTML = `
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${user.roles
            ? user.roles.map(role => role.roleName.substring(5)).join(', ') : 'No roles'}</td>
            </tr>
    `
    } catch (error) {
        console.error('Ошибка при получении данных пользователя:', error);
        document.getElementById('usernamePlaceholder').textContent = 'Error fetching user';
        document.getElementById('userRoles').textContent = '';
    }
}

document.addEventListener('DOMContentLoaded', fetchCurretUser);