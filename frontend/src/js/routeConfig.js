const routeLookup = path => {
    switch (path) {
        //Views
        case "": return "home";
        case "/edit-custom-view": return "editCustomView";
        case "/login": return "Login";

        //components
        case "navBar": return "navBar";
        case "authModal": return "authModal";

        default: return "404";
    }
};

const route = path => `/GvKqfsJV/${routeLookup(path)}.hjc`
