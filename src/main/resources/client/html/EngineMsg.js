
function sendText() {
    var mes = document.getElementById("messageTextArea").value;

    var newDiv = document.createElement('div');
    document.getElementById("messageArea").appendChild(newDiv);
    newDiv.innerHTML = mes;
}

