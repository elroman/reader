var Checker = (function () {
    var module = {};

    module.checkAnswer = function () {

        var answer = $("#answer")[0].value;
        var secretAnswer = $("#secret_answer")[0].value;

        if (secretAnswer != answer) {
            module.toastrMessage("wrong answer!!!!");

        } else {
            module.toastrMessage("correct! ");

        }
    };


    module.toastrMessage = function (message) {
        toastr.success(message);
        toastr.options.timeOut = 2500;

    };

    return module;
}());
