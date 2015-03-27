var App = {};

$('.vessel h2').click(function(ev){
    ev.preventDefault();
    $(ev.target).closest('.vessel').find('form.edit').toggle();
});