/**
 * Created by lee on 11/11/16.
 */
$(document).ready(function() {

    $('.container').mouseenter(function () {         //hover animation home button
        $('html').css('background-image', 'url("assets/PlayMeInLogoBlur.jpg")');
        $('#logo2').removeClass('transparent');
    });

    $('.container').mouseleave(function () {
        $('html').css('background-image', 'url("assets/PlayMeInLogo.jpg")');
        $('#logo2').addClass('transparent');
    });
});