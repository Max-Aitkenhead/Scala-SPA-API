let componentInit = () => {};

loadComponent('navBar', 'nav');

const loadModal = (path) => {
    console.log("loadModal");
    loadComponent(path, 'modalBase');
}
