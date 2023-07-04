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

    //Affichage des notes
    let notesModal = document.querySelector("#notesModal");
    if(notesModal != null) {
        notesModal.addEventListener('shown.bs.modal', function () {
            let xhr = new XMLHttpRequest();
            xhr.open('GET', '/dossiers/notes/' + notesModal.getAttribute("ea-dossier-id"), true);
            xhr.onreadystatechange = function () {
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status === 200) {
                        document.querySelector("#notesBody").innerHTML = xhr.responseText;
                    } else {
                        console.error('Une erreur s\'est produite.');
                    }
                }
            };
            xhr.send();
        });
    }

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
                document.getElementById("end-date").required = true;
            } else {
                document.getElementById("amenagement-end-date").classList.add("d-none");
                document.getElementById("end-date").required = false;
            }
        });
    }

    //Side hamburger button
    let sideFilterButton = document.getElementById("side-filter-button");
    if(sideFilterButton != null) {
        sideFilterButton.addEventListener("click", function (e) {
            let filterForm = document.getElementById("filter-form");
            let sideFilter = document.getElementById("side-filter");
            let sideTitle = document.getElementById("side-filter-title");
            let submitFilter = document.getElementById("submit-filter");
            sideFilter.classList.toggle("agape-side");
            sideFilter.classList.toggle("agape-side-closed");
            if(!filterForm.classList.contains("d-none")) {
                filterForm.classList.add("d-none");
                sideTitle.classList.add("d-none");
                submitFilter.classList.add("d-none");
            } else {
                filterForm.classList.remove("d-none");
                sideTitle.classList.remove("d-none");
                submitFilter.classList.remove("d-none");
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

    //Gestion automatique des slimselect avec search
    document.querySelectorAll(".agape-slim-select-search").forEach(function (element) {
        if(element.id !== '') {
            console.info("enable slimselect search on : " + element.id);
            new SlimSelect({
                select: '#' + element.id,
                settings: {
                    openPosition: 'down',
                    placeholderText: 'Choisir',
                    searchPlaceholder: 'Rechercher',
                }
            });
            //Hack slimselect required
            element.style.display = "block";
            element.style.position = "absolute";
            element.style.marginTop = "15px";
            element.style.opacity = 0;
            element.style.zIndex = -1;
        }
    });

    //Gestion automatique des slimselect
    document.querySelectorAll(".agape-slim-select").forEach(function (element) {
        if(element.id !== '') {
            console.info("enable slimselect on : " + element.id);
            new SlimSelect({
                select: '#' + element.id,
                settings: {
                    showSearch: false,
                    placeholderText: 'Choisir',
                }
            });
            //Hack slimselect required
            element.style.display = "block";
            element.style.position = "absolute";
            element.style.marginTop = "15px";
            element.style.opacity = 0;
            element.style.zIndex = -1;
        }
    });

    document.querySelectorAll(".agape-slim-select-sm").forEach(function (element) {
        if(element.id !== '') {
            console.info("enable slimselect on : " + element.id);
            let slimSelect = new SlimSelect({
                select: '#' + element.id,
                settings: {
                    showSearch: false,
                    placeholderText: 'Choisir',
                }
            });
            //Hack slimselect required
            element.style.display = "block";
            element.style.position = "absolute";
            element.style.marginTop = "15px";
            element.style.opacity = 0;
            element.style.zIndex = -1;
            let slimId = slimSelect.settings.id;
            let slimSelectDivs = document.querySelectorAll("div[data-id='" + slimId + "']");
            slimSelectDivs.forEach(function(slimSelectDiv) {
                let test = slimSelectDiv.querySelector(".ss-arrow");
                if(test == null) {
                    slimSelectDiv.classList.remove("form-select")
                } else {
                    test.remove();
                }
            });

        }
    });

    //Gestion du formulaire enquete
    let codMeae = document.getElementById("codMeae")
    if(codMeae != null) {
        let codMeaeSlim = new SlimSelect({
            select: '#codMeae',
            settings: {
                showSearch: false,
                placeholderText: 'Choisir',
            },
            events: {
                afterChange: (newVals) => {
                    if(newVals.length > 1) {
                        newVals = newVals.filter(newVal => newVal.value !== "AE0");
                        codMeaeSlim.setSelected(newVals.map(newVal => newVal.value));
                    } else if(newVals.length === 0) {
                        codMeaeSlim.setSelected("AE0");
                    }
                }
            }
        });
        //Hack slimselect required
        codMeae.style.display = "block";
        codMeae.style.position = "absolute";
        codMeae.style.marginTop = "15px";
        codMeae.style.opacity = 0;
        codMeae.style.zIndex = -1;
    }

    let codMeaa = document.getElementById("codMeaa")
    if(codMeaa != null) {
        new SlimSelect({
            select: '#codMeaa',
            settings: {
                showSearch: false,
                placeholderText: 'Choisir',
            },
            events: {
                afterChange: (newVals) => {
                    if(newVals.filter((v) => v.value === "AAO").length > 0) {
                        document.getElementById("autAADiv").classList.remove("d-none");
                    } else {
                        document.getElementById("autAADiv").classList.add("d-none");
                        document.getElementById("autAA").value = "";
                    }
                }
            }
        });
        //Hack slimselect required
        codMeaa.style.display = "block";
        codMeaa.style.position = "absolute";
        codMeaa.style.marginTop = "15px";
        codMeaa.style.opacity = 0;
        codMeaa.style.zIndex = -1;
    }

    let codFil = document.getElementById("codFil");
    if(codFil != null) {
        let codFmt = new SlimSelect({
           select: '#codFmt',
            settings: {
                showSearch: false,
                placeholderText: 'Choisir',
                searchText: '',
                searchPlaceholder: 'Rechercher'
            },
            events: {
                afterChange: (newVal) => {
                    console.log(newVal[0].value);
                    fetch('/ws-secure/enquete/cod-sco/?codFmt=' + newVal[0].value)
                        .then((response) => response.json())
                        .then(function (data) {
                            console.log(data);
                            if(data.length > 1) {
                                codSco.setData(data);
                                codSco.enable();
                            } else {
                                codSco.setData([{text: '', value: ''}]);
                                codSco.disable();
                            }
                        });
                }
            }
        });
        codFmt.disable();
        let codSco = new SlimSelect({
            select: '#codSco',
            settings: {
                showSearch: false,
                placeholderText: 'Choisir',
                searchText: '',
                searchPlaceholder: 'Rechercher'
            }
        });
        codSco.disable();
        codFil.addEventListener("change", function (event) {
            fetch('/ws-secure/enquete/cod-fmt/?codFil=' + codFil.value)
                .then((response) => response.json())
                .then(function(data){
                    codFmt.setData(data);
                    codFmt.enable();
                });
        });

    }

    let am0On = document.getElementById("AM0On")
    if(am0On != null) {
        am0On.addEventListener("click", function (event) {
            document.getElementById("codAmLDiv").classList.add("d-none");
        });
    }
    let am0Off = document.getElementById("AM0Off")
    if(am0Off != null) {
        am0Off.addEventListener("click", function (event) {
            document.getElementById("codAmLDiv").classList.remove("d-none");
        });
    }

    let asOn = document.getElementById("ASOn")
    if(asOn != null) {
        asOn.addEventListener("click", function (event) {
            document.getElementById("codPfasDiv").classList.remove("d-none");
        });
    }
    let asOff = document.getElementById("ASOff")
    if(asOff != null) {
        asOff.addEventListener("click", function (event) {
            document.getElementById("codPfasDiv").classList.add("d-none");
        });
    }

    let ahs0On = document.getElementById("AHS0On")
    if(ahs0On != null) {
        ahs0On.addEventListener("click", function (event) {
            document.getElementById("codMeahFDiv").classList.remove("d-none");
        });
    }
    let ahs0Off = document.getElementById("AHS0Off")
    if(ahs0Off != null) {
        ahs0Off.addEventListener("click", function (event) {
            document.getElementById("codMeahFDiv").classList.add("d-none");
        });
    }

    //Gestion des float button
    let saveBtn = document.getElementById("save-btn");
    if(saveBtn != null) {
        saveBtn.addEventListener("click", function (e) {
            let formSubmitBtn = document.querySelectorAll(`[class*="form-submit-btn"]`)
            if(formSubmitBtn.length > 0) {
                formSubmitBtn[0].click();
            }
        });
    }

});

function unLockClassification() {
    document.getElementById("classificationDiv").classList.remove("d-none");
    document.getElementById("classification").required = true;
}

function lockClassification() {
    document.getElementById("classificationDiv").classList.add("d-none");
    document.getElementById("classification").required = false;
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
