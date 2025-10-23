window.addEventListener("load", function(){
    var confirmLinks = document.querySelectorAll("a.confirm-link");
    for(var i = 0; i < confirmLinks.length; i++)
    {
        confirmLinks[i].addEventListener("click", function(e){
            var comment = this.dataset.comment; //data-comment 내용 읽기

            if(comment == undefined)
            {
                comment = "정말 이동하시겠습니까";
            }

            var choice = window.confirm(comment);
            if(choice == false)
            {
                e.preventDefault();
            }

        });
    }
});