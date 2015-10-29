var host = "http://localhost:8080";
var url = host + '/WSRest/rest';
var workflowRestUrl = url + '/tavernaworkflows';
var servicesRestUrl = url + '/services';

var loadingAnimation = '<p><img src="images/loading.gif"/></p>';

/* HTML Elements */
/* SOAP Services */
var listOfServicesHtmlElement = '#listOfServices';
var listOfServicesHtmlLinkElement = '#listOfServicesLink';
var currentServiceHtmlElement = '#currentService';
var currentServiceHtmlLinkElement = '#currentServiceLink';
var resultsHtmlElement = '#results';
var resultsHtmlLinkElement = '#resultsLink';

/* Workflows */
var workflowUploaderLinkElement = '#workflowUploaderLink';
var currentWorkflowElement = '#currentWorkflowForm';
var currentWorkflowLinkElement = '#currentWorkflowLink';
var resultsHtmlElement = '#results';
var resultsHtmlLinkElement = '#resultsLink';
/****************/

var servicesJSON = undefined;
var uploadWorkflowForm = undefined;
var workflowJSON = undefined;

function urlify(text) {
    var urlRegex = /\b((?:[a-z][\w-]+:(?:\/{1,3}|[a-z0-9%])|www\d{0,3}[.]|[a-z0-9.-]+[.][a-z]{2,4}\/)(?:(?:[^\s()<>.]+[.]?)+|((?:[^\s()<>]+|(?:([^\s()<>]+)))))+(?:((?:[^\s()<>]+|(?:([^\s()<>]+))))))/gi;

    return text.replace(urlRegex, function(url) {
        return '<a target="_blank" href="' + url + '">' + url + '</a>';
    });
}
