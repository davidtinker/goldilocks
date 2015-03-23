var App = {};

$('.vessel a.edit').click(function(ev){
    ev.preventDefault();
    $(ev.target).closest('.vessel').find('form.edit').toggle();
});