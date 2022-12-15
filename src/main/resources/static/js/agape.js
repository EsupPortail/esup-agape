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
        if(element.classList.contains("agape-amenagement-textarea")) {
            textAreaAdjust(element, 18);
            element.addEventListener("keyup", e => textAreaAdjust(e.target, 18));
        } else {
            textAreaAdjust(element, 30);
            element.addEventListener("keyup", e => textAreaAdjust(e.target, 30));
        }
    });

    //Activation suiviHandiSup
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
                document.querySelectorAll(`[id^="periode-` + monthNum +`-"]`).forEach(function (input) {
                    input.disabled = false;
                    if(input.id.includes("edit") || input.id.includes("submit") || input.id.includes("delete")) {
                        input.classList.toggle('d-none');
                    }
                });
            });
        }
    });

    //Gestion des aménagements date
    let typeAmenagementInput = document.getElementById("typeAmenagement");
    if(typeAmenagementInput != null) {
        typeAmenagementInput.addEventListener("change", function (e) {
            if (this.value === "DATE") {
                document.getElementById("amenagement-end-date").classList.remove("d-none");
            } else {
                document.getElementById("amenagement-end-date").classList.add("d-none");
            }
        });
    }

    //Gestion des aménagements autorisation classifications

    let autorisationOui = document.getElementById("autorisationOui");
    let autorisationNon = document.getElementById("autorisationNon");
    if(autorisationOui != null && autorisationNon != null) {
        autorisationOui.addEventListener("click", function () {
            unLockClassification();
        });
        autorisationNon.addEventListener("click", function () {
            unLockClassification();
        });
        if(autorisationOui.checked || autorisationNon.checked) {
            unLockClassification();
        }
    }

    let autorisationNc = document.getElementById("autorisationNc");
    if(autorisationNc != null) {
        autorisationNc.addEventListener("click", function () {
            lockClassification();
        });
    }

    //Gestion automatique des slimselect
    document.querySelectorAll(`[class*="agape-slim-select"]`).forEach(function (element) {
        console.info("enable slimselect on : " + element.id);
        new SlimSelect({
            select: '#' + element.id,
            settings: {
                placeholderText: 'Choisir',
                searchPlaceholder: 'Rechercher',
            }
        });
        //Hack slimselect required
        element.style.display = "block";
        element.style.position = "absolute";
        element.style.marginTop = "15px";
        element.style.opacity = 0;
    });

});

function unLockClassification() {
    document.getElementById("classificationDiv").classList.remove("d-none");
    document.getElementById("classification").required = true;
}

function lockClassification() {
    document.getElementById("classificationDiv").classList.add("d-none");
}

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

function textAreaAdjust(element, lineHeight) {
    let text = element.value;
    let lines = text.split(/\r|\r\n|\n/);
    let count = lines.length;
    element.style.height = (lineHeight * count) + "px";
}