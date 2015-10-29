function getWorkflowForm() {
    var html = '';

    html += '<form id="WorkflowRunner" role="form" action=""><fieldset><h3>Taverna workflow <b>' + workflowJSON.name + '</b></h3>';

    $.each(workflowJSON.inputs, function (i, input) {
        html += '<div class="form-group"><label for="' + input.name + '">' + input.name + '(' + input.documentation + ')';
        // html += '<b>isBinary?:</b>' + input.isBinary;
        html += '</label>';
        html += '<input type="text" class="form-control" name="' + input.name + '" value="' + input.exampleValue + '">';
        html += '</div>';
    });
    html += '<button type="submit" id="run" class="btn btn-default" '
        + 'onClick="executeWorkflow(); return false">Run!!'
        + '</button>';
    html += '</fieldset></form>'; // Operation form

    $(currentWorkflowElement).html(html);
    $(currentWorkflowLinkElement).click();
}

function executeWorkflow() {
    var data = $('#WorkflowRunner').serializeArray();
    $(resultsHtmlElement).html(loadingAnimation);
    $(resultsHtmlLinkElement).click();
    var data4Rest = [];

    $.each(data, function (i, element) {
        data4Rest[i] = {};
        data4Rest[i].name = element.name;
        data4Rest[i].values = [];
        data4Rest[i].values.push(element.value);
    });

    uploadWorkflowForm.append('inputs', JSON.stringify(data4Rest));

    $.ajax({
        type: "POST",
        url: workflowRestUrl + '/workflowrunner',
        data: uploadWorkflowForm,
        processData: false,
        contentType: false,
        success: function (responseJSON) {
            var html = '';

            $.each(responseJSON, function (i, tavernaworkflowoutput) {
                html += '<h2>' + tavernaworkflowoutput.name + '</h2>';
                html += '<div  class="span5">';
                $.each(tavernaworkflowoutput.outputs, function (i, output) {
                    html += '<div class="span3">';
                    html += '<code>' + urlify(output.url) + '</code>';
                    html += '<div class="span3 offset3">';
                    html += urlify(output.value);
                    html += '</div>';
                    html += '</div>';
                });
                html += '</div>';
            });
            $(resultsHtmlElement).html(html);
        },
        error: function () {
            $(resultsHtmlElement).html("<h2>ERROR</h2>");
        }
    });

    return false;
}

jQuery(document).ready(function () {
    $(workflowUploaderLinkElement).click();
    $(workflowUploaderLinkElement).on("click", function () {
        this.click();
        $('#selectFile').html("Select a taverna workflow file");
        $('#selectFile').removeClass('disabled');
    });

    $('#upload-button').hide();

    $('#selectFile').on("click", function () {
        $('#selectFile').html("Press Upload button");
        $('#selectFile').addClass('disabled');
        $('#upload-button').show();
    });
    $('#uploadWorkflowForm')
        .submit(function (e) {
            $('#uploadLoading').show();
            var formElement = this;
            var filename = formElement[0].files[0].name;
            var previewForm = new FormData();

            uploadWorkflowForm = new FormData(this);
            previewForm.append('t2flow', formElement[0].files[0]);
            previewForm.append('width', 700);

            $.ajax({
                url: workflowRestUrl + '/workflowparser',
                type: 'POST',
                data: uploadWorkflowForm,
                processData: false,
                contentType: false,
                success: function (response) {
                    $('#uploadLoading').hide();
                    workflowJSON = response;

                    $('#workflowPreview').html('<div class="img-responsive">' + workflowJSON.workflowPreview + '</div>');
                    getWorkflowForm();

                    $(currentWorkflowLinkElement).click();
                },
                error: function () {
                    console.log("<h2>ERROR</h2>");
                }
            });

            e.preventDefault();
        }
    );
    $('#myExperimentForm')
        .submit(function (e) {
            var myExperimentLogin = new FormData(this);
            var values = $(this).serializeArray();
            var myExperimentUser = values[0];
            var myExperimentPassword = values[1];

            console.log(workflowRestUrl + '/myexperiment/' + myExperimentUser.value);

            $.ajax({
                url: workflowRestUrl + '/myexperiment/',
                type: 'POST',
                data: myExperimentLogin,
                processData: false,
                contentType: false,
                success: function (response) {

                    $(currentWorkflowLinkElement).click();
                },
                error: function () {
                    console.log("<h2>ERROR</h2>");
                }
            });
            e.preventDefault();
        }
    );
});