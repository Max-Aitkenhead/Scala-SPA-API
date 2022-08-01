componentInit = () => {
    M.updateTextFields();
}

const getPageJson = () => ({
    name: getInput("nameInput"),
    path: getInput("pathInput"),
    markup: getInput("markupInput"),
    css: getInput("cssInput"),
    js: getInput("jsInput")
});

const getInput = (id) => document.getElementById(id).value;

const showPreview = () => {
    console.log(getPageJson());
    document.getElementById("markupInputForm").style.display = "none";
    document.getElementById("generateContent").innerHTML = parseRawMarkup(getPageJson().markup);
    document.getElementById("markupPreview").style.display = "block";
};

const hidePreview = () => {
    document.getElementById("markupPreview").style.display = "none";
    document.getElementById("markupInputForm").style.display = "block";
}


