componentInit = () => {
    console.log("modal innited");
    const elem = document.getElementById('authModal');
    const instance = M.Modal.init(elem, {});
    instance.open();
    M.updateTextFields();
}


