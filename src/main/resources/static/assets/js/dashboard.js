$(document).ready(function () {
    const $menuNavItems = $(".menu-nav-item");
    const $menuBodyDivs = $(".menu-body-div");
    const $navLinks = $menuNavItems.find("a.nav-link");

    $menuNavItems.click(function () {
        // Hide all divs first
        $menuBodyDivs.hide();

        // Remove 'active' class from all nav-links
        $navLinks.removeClass("active");

        // Add 'active' class to the clicked nav-link
        $(this).find("a.nav-link").addClass("active");

        // Get the data-target attribute value of the clicked list item
        const targetDiv = $(this).data("target");

        // Show the selected div
        $("." + targetDiv + "-body-div").show();
    });
});
