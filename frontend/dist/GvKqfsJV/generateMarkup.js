const parseRawMarkup = input => {
    let first = 0;
    let regArr = [];
    let htmlstr = "";
    const reg = /\[\[(.*?)\]\]/gm;
    while ((regArr = reg.exec(input)) !== null) {

        htmlstr += input.substring(first, (reg.lastIndex - regArr[1].length - 4))

        htmlstr += getElement(input.substring(reg.lastIndex - regArr[1].length - 2, reg.lastIndex - 2))
        first = reg.lastIndex;
    }
    if (first != input.length) htmlstr += input.substring(first, input.length);

    console.log(htmlstr);
    return htmlstr;
}

const getElement = (string) => {
    const split = string.split("~");
    switch (split[0].toLowerCase()) {
        case 'link': return`<button class="btn" onclick="changePage('/')">Home</button>`;
        case 'container open': return'<div class="container">';
        case 'container close': return '</div>';
        case 'linebreak': return 'this is a linebreak to be inserted!';
        default: return string + ' - error processing element';
    }
}