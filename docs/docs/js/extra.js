$(document).ready(function() {
    $("p.admonition-title").each(function(index, value) {
        var str = $(this).text();
        var res = '<i class="fa fa-exclamation-circle fa-fm"></i> &nbsp;'.concat(str);
        $(this).html(res);        
    });

    $("div .admonition.warning").addClass("alert").addClass("alert-warning");
    $("div .admonition.danger").addClass("alert").addClass("alert-danger");
    $("div .admonition.info").addClass("alert").addClass("alert-info");
    $("div .admonition.success").addClass("alert").addClass("alert-success");
});
