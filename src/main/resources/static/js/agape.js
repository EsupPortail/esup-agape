
document.addEventListener("DOMContentLoaded", function(event) {

    //Lancement automatique du toast
    const toastDiv = document.getElementById('toast');
    if(toastDiv != null) {
        const toast = new bootstrap.Toast(toastDiv);
        toast.show();
    }

    //Gestion de la vue situation
    document.getElementById('unlock-dossier').addEventListener('click', function (e) {
        e.target.classList.toggle('d-none');
        document.getElementById('lock-dossier').classList.toggle('d-none');
        let form = document.getElementById('form-situation');
        [...form.elements].forEach(item => {
            item.disabled = false;
        });
    });

    document.getElementById('lock-dossier').addEventListener('click', function (e) {
        e.target.classList.toggle('d-none');
        let form = document.getElementById('form-situation');
        [...form.elements].forEach(item => {
            item.disabled = true;
        });
        document.getElementById('unlock-dossier').classList.toggle('d-none');
        document.getElementById('unlock-dossier').disabled = false;

    });

});
