window.onscroll = function() {
    if (document.body.scrollTop < 10) {
        chatViewController.loadPreviousPageOfMessages();
    }
}