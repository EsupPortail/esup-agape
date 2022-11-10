document.addEventListener("DOMContentLoaded", function(event) {

    //Lancement automatique du toast
    const toastDiv = document.getElementById('toast');
    if(toastDiv != null) {
        const toast = new bootstrap.Toast(toastDiv);
        toast.show();
    }

    Array.prototype.slice.call(document.getElementsByTagName('textarea')).forEach(function (element) {
        textAreaAdjust(element);
    });

});

function lockForm() {
    location.reload();
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

function textAreaAdjust(element) {
    let text = element.value;
    let lines = text.split(/\r|\r\n|\n/);
    let count = lines.length;
    element.style.height = (30*count)+"px";
}