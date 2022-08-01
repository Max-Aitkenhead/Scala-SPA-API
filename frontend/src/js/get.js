const apiGet = (endpoint, callBack = ()=>{}) => {
    const url = `http://${window.location.host}/api/${endpoint}/`;
    console.log(url);
    return fetch(url, {
        method: 'GET'
    }).then(data => data.json().then(json => {
        // updateToken(json.token);
        // if(json.hasOwnProperty('user')) loginAction(json.user);
        // if(json.hasOwnProperty('log_out')) logoutAction();
        console.log(json);
        callBack(json);
    }))

}