document.addEventListener("DOMContentLoaded", function(event) {

    //Lancement automatique du toast
    const toastDiv = document.getElementById('toast');
    if(toastDiv != null) {
        const toast = new bootstrap.Toast(toastDiv);
        toast.show();
    }

});

function lockForm(button) {
    let formName = button.form.getAttribute("id").replace("form-" , "");
    button.classList.toggle('d-none');
    let unlockButton = document.getElementById('unlock-' + formName);
    unlockButton.classList.toggle('d-none');
    document.getElementById('submit-' + formName).classList.toggle('d-none');
    let form = document.getElementById('form-' + formName);
    [...form.elements].forEach(item => {
        item.disabled = true;
    });
    unlockButton.disabled = false;
    let closeButton = document.getElementById('close-' + formName);
    if(closeButton != null) {
        closeButton.classList.toggle('d-none');
        closeButton.disabled = false;
    }
}

function unlockForm(button) {
    let formName = button.form.getAttribute("id").replace("form-" , "");
    button.classList.toggle('d-none');
    document.getElementById('lock-' + formName).classList.toggle('d-none');
    document.getElementById('submit-' + formName).classList.toggle('d-none');
    let closeButton = document.getElementById('close-' + formName);
    if(closeButton != null) {
        document.getElementById('close-' + formName).classList.toggle('d-none');
    }
    let form = document.getElementById('form-' + formName);
    [...form.elements].forEach(item => {
        item.disabled = false;
    });

}