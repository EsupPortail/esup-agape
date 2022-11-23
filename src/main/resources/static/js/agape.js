document.addEventListener("DOMContentLoaded", function(event) {

    //Lancement automatique du toast
    const toastDiv = document.getElementById('toast');
    if(toastDiv != null) {
        const toast = new bootstrap.Toast(toastDiv);
        toast.show();
    }

    //Ajustement automatique de la hauteur des textarea
    Array.prototype.slice.call(document.getElementsByTagName('textarea')).forEach(function (element) {
        console.info("Enable textarea auto adjust on " + element.id);
        textAreaAdjust(element);
        element.addEventListener("keyup", e => textAreaAdjust(e.target));
    });

    //Activation suiviHansiSup
    let suiviHandisupOui = document.getElementById("suiviHandisupOui");
    if(suiviHandisupOui != null) {
        suiviHandisupOui.addEventListener("click", function () {
            document.getElementById("typeSuiviHandisupDiv").classList.remove("d-none");
        });
    }

    let suiviHandisupNon = document.getElementById("suiviHandisupNon");
    if(suiviHandisupNon != null) {
        suiviHandisupNon.addEventListener("click", function () {
            document.getElementById("typeSuiviHandisupDiv").classList.add("d-none");
        });
    }

    //Gestion des feuilles d’heures
    document.querySelectorAll(`[id^="periode-"]`).forEach(function (element) {
        if(element.id.includes("edit")) {
            let monthNum = element.id.split("-")[1];
            element.addEventListener("click", function () {
                document.querySelectorAll(`[id^="periode-"]`).forEach(function (input) {
                    input.disabled = true;
                });
                document.querySelectorAll(`[id^="periode-` + monthNum +`"]`).forEach(function (input) {
                    input.disabled = false;
                    if(input.id.includes("edit") || input.id.includes("submit")) {
                        input.classList.toggle('d-none');
                    }
                });
            });
        }
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
        if(item.readOnly === undefined || item.readOnly === false) {
            item.disabled = false;
        } else {
            let editBtn = document.getElementById(item.id + "Edit");
            if(editBtn != null) {
                editBtn.classList.remove("d-none");
            }
        }
    });
}

function toggleInputLock(id) {
    document.getElementById(id).toggleAttribute('disabled');
    document.getElementById(id).toggleAttribute('readOnly');
    document.getElementById(id + "Edit").remove();
}

function textAreaAdjust(element) {
    let text = element.value;
    let lines = text.split(/\r|\r\n|\n/);
    let count = lines.length;
    element.style.height = (30*count)+"px";
}