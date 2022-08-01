let currentPath = window.location.pathname;

const changePage = (newPath) => {
    if (currentPath !== newPath) {
        currentPath = newPath;
        console.log(`changing path to ${newPath}`);
        history.pushState({ path: newPath }, "", newPath)
        loadView(newPath);
    } else console.log("repeated route");
}

window.addEventListener("popstate", () => {
    console.log("back button pressed");
    loadView(window.location.pathname)
});

window.addEventListener("load", () => {
    console.log("page load");
    loadView(window.location.pathname);
});

const loadView = (path) => fetchPageData(path, renderElement('view'));

const loadComponent = (component, domElementId) => fetchPageData(component, renderElement(domElementId));

const renderElement = parentId => pageData => {
    console.log(pageData);
    document.getElementById(parentId).innerHTML = pageData.markup;
    if (pageData.hasOwnProperty('js')) {
        const script = document.createElement('script');
        script.innerHTML = pageData.js;
        document.getElementById(parentId).appendChild(script);
        // componentCache.set(path, {
        //     markup: splitData[0],
        //     css: splitData[2],
        //     init: componentInit
        // });
    }
    componentInit();
}

const fetchPageData = (path, callback) => {
    currentPath = path;
    if (componentCache.has(path)) callback(componentCache.get(path));
    else fetch(route(pathStripSlash(path)))
        .then(response => response.text())
        .then(data => {
            const splitData = data.split("£££££");
            callback({
                markup: splitData[0],
                js: splitData[1],
                css: splitData[2],
            });
        });
}



const pathStripSlash = (path) => path.substring(path.length - 1, path.length) === "/" ? path.substring(0, path.length - 1) : path;
