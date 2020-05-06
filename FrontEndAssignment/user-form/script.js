document.addEventListener("DOMContentLoaded", performFormAction);

function performFormAction(event) {
    const userForm = document.getElementById('user-form');
    if (userForm) {
        userForm.addEventListener('submit', validateForm);
    }
}

function validateForm(event) {
    event.preventDefault();
    event.stopPropagation();
    const formElements = event.currentTarget.elements;
    if (formElements && formElements.length) {
        for (let i = 0; i < formElements.length; i++) {
            const name = formElements[i].name;
            if (name) {
                const displayElement = document.getElementById('user-' + name);
                if (displayElement) {
                    displayElement.innerText = formElements[i].value;
                }
            }
        }
    }
    // Rest form data
    event.currentTarget.reset();
}