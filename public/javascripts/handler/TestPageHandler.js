var TestPageHandler = (function () {
    var module = {};

    init();

    function init() {

        var doCheckAnswerButton = $("#doCheckAnswer");
        var answerField = $("#answer");

        doCheckAnswerButton.click(function () {
            Checker.checkAnswer();
        });

        answerField.keydown(function (event) {
            if (event.keyCode == 13) {

                Checker.checkAnswer();
                //location.reload();  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
            }
        });
    };

    return module;
}());
