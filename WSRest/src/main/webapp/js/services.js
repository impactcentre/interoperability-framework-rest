var loadServices =
        $.ajax({
            url: servicesRestUrl,
            type: 'GET',
            data: '',
            dataType: 'jsonp',
            contentType: "text/plain",
            //contentType: "application/json; charset=utf-8",
            beforeSend : function(xhr, opts){
                //xhr.overrideMimeType( "text/plain; charset=x-user-defined" );
            },
            async: true,
            success: function (services) {
                var html = '';
                servicesJSON = services;

                html += '<div class="table-responsive"><table class="table table-hover">';
                html += '<thead>' +
                    '<tr>' +
                    '<th></th>' +
                    '<th>Name</th>' +
                    '<th>Description</th>' +
                    //'<th>URLs</th>' +
                    '<th>Operations</th>' +
                    '</tr>' +
                    '</thead>';

                $.each(servicesJSON, function (i, service) {
                    html += '<tr>';
                    html += '<td>' + (service.id + 1) + '</td>';
                    html += '<td>' + service.name + '</td>';
                    html += '<td>' + service.description + '</td>';
                    //html += '<td>';
                    //html += '<ul><li><a target="_blank" href="' + service.wsdl + '">' + service.wsdl + '</a></li>';
                    //html += '<li><a target="_blank" href="' + servicesRestUrl + '/' + service.id + '">' + servicesRestUrl + '/' + service.id + '</a></li>';
                    //html += '</ul></td>';
                    html += '<td>';
                    $.each(service.soapService.operations, function (operationId, operation){
                        html += '<button type="button" class="btn btn-info" data-toggle="collapse" data-target="#currentService"'
                             +  ' onClick="getServiceForm(\'' + service.id + '\', \'' + operationId + '\')">' + operation.name
                             +  '</button>';
                        html += '<p><b>inputs: </b>';
                        html += '<ul>';
                        $.each(operation.inputs, function (k, input) {
                            html += '<li>' + input.name + ': ' + input.documentation + '</li>';
                        });
                        html += '</p>';
                        html += '</ul>';
                    });
                    html += '</td>';
                    html += '</tr>';
                });

                html += '</table></div>';

                return html;
            },
            error: function (e) {
                return 'ERROR: ' + e.message;
            }
        });

function getServiceForm(serviceId, operationId ) {
    var service = servicesJSON[serviceId];
    var operation = service.soapService.operations[operationId];
    var html = '';

    html += '<form id="' + operation.name + '" role="form" action=""><fieldset><h3>Operation <b>' + operation.name + '</b></h3>';

    $.each(operation.inputs, function(i, input){
        html += '<div class="form-group"><label for="' + input.name + '">' + input.name + '(' + input.documentation + ')';
        // html += '<b>isBinary?:</b>' + input.isBinary;
        html += '</label>';

        if (input.posibleValues.length > 0) { //select
            html += '<select class="form-control" name="' + input.name + '"';
            if (input.isMultivaluated)
                html += 'multiple="' + input.isMultivaluated + '">';
            else
                html += '>';
            $.each(input.posibleValues, function (i, element) {
            	if(input.defaultValue == element)
            		html += '<option value="' + element + '" selected>' + element + '</option>';
            	else
            		html += '<option value="' + element + '" >' + element + '</option>';
            });
            html += '</select>';
        }
        else //Input, url?
        {
            html += '<input type="text" class="form-control" name="' + input.name + '" value="' + input.defaultValue + '">';
        }
        html += '</div>';
    });
    html += '<button type="submit" id="run" class="btn btn-default" '
         +  'onClick="executeService(\'' + serviceId + '\', \'' + operation.name + '\'); return false">Run!!'
         +  '</button>';
    html += '</fieldset></form>'; // Operation form

    $(currentServiceHtmlElement).html(html);
    $(currentServiceHtmlLinkElement).click();
}

function executeService(serviceId, formId)
{
    var data = $('#' + formId).serializeArray();
    var data4Rest = [];

    $(resultsHtmlElement).html(loadingAnimation);
    $(resultsHtmlLinkElement).click();

    $.each(data, function (i, element) {
        data4Rest[i] = {};
        data4Rest[i].name = element.name;
        data4Rest[i].values = [];
        data4Rest[i].values.push(element.value);
    });

    $.ajax({
        type: "POST",
        url: servicesRestUrl + '/' + serviceId + '/' + formId,
        data: JSON.stringify(data4Rest),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        async: true,
        success: function (responseJSON) {
            var html = '';

            html += '<h2>Results</h2>';
            html += '<div  class="span5">';
            $.each(responseJSON, function (i, output) {
                html += '<div class="span3">';
                html += '<em><b>' + output.name + '</b></em>';
                html += '<div class="span3 offset3">';
                html += urlify(output.value);
                html += '</div>';
                html += '</div>';
            });
            html += '</div>';
            $(resultsHtmlElement).html(html);
        },
        error: function () {
            $(resultsHtmlElement).html("<h2>ERROR</h2>");
        }
    });

    return false;
}

jQuery(document).ready(function () {
    $(listOfServicesHtmlElement).html(loadingAnimation);
    $(listOfServicesHtmlLinkElement).click();
    loadServices
        .done(function(services) {
            $(listOfServicesHtmlElement).html(this.success(services));
        })
        .fail(function( jqXHR, textStatus ) {
            $(listOfServicesHtmlElement).html(this.error());
        });
});