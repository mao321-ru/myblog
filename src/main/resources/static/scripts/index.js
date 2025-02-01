const addPopupPost = document.querySelector('.popup_type_add')
const btnAddPost = document.querySelector('.header__post-add')
const btnPopupClose = document.querySelector('.popup__close')

const inpPopupTitle = document.querySelector('.popup__title_input')

const btnPopupSave = document.querySelector('.popup__save')
const frmAddPost = document.querySelector('.popup__form')
const inputsPopup = document.querySelectorAll('.popup__input')

// Поиск по тегам
const dropdownButton = document.querySelector(".dropdown__button");
const optionsMenu = document.getElementById("optionsMenu");
const dropdownItems = document.querySelectorAll(".menu-item");
const dropdownActiveText = document.querySelector(".dropdown__text");
const btnFilterPostsSave = document.querySelector(".filter__save");
const inpFilterPosts = document.querySelector(".filter__tag");
const inpPageSize = document.querySelector(".filter__psize");
const frmFilter = document.querySelector('.filter__form')

// открытие/закрытие выпадающего списка
dropdownButton.addEventListener("click", () => {
    if (optionsMenu.style.display === "block") {
        optionsMenu.style.display = "none";
    } else {
        optionsMenu.style.display = "block";
    }
});

dropdownItems.forEach((item) => {
    item.addEventListener("click", () => {
        dropdownActiveText.innerText = item.innerText
        optionsMenu.style.display = "none";
    });
});

// закрытие выпадающего списка при нажатии на внешнюю область
document.addEventListener("click", (event) => {
    if (!event.target.closest(".dropdown")) {
        optionsMenu.style.display = "none";
    }
});

// отправляем запрос с фильтрацией постов
btnFilterPostsSave.addEventListener("click", () => {
    inpPageSize.value = dropdownActiveText.innerText
    frmFilter.submit();
});

// Форма добавления поста

// Открываем форму
btnAddPost.addEventListener('click', (e) => {
    e.preventDefault();
    addPopupPost.classList.add('popup_opened')
})

// Закрываем форму
btnPopupClose.addEventListener('click', (e) => {
    e.preventDefault();
    addPopupPost.classList.remove('popup_opened')
})

// Сохраняем пост
btnPopupSave.addEventListener('click', (e) => {
    e.preventDefault();
    // проверяем, введен ли заголовок поста
    if ( inpPopupTitle.value !== '') {
        frmAddPost.submit();
    }

    //закрываем форму
    addPopupPost.classList.remove('popup_opened')
})

//обработка Enter для формы
inputsPopup.forEach((input)=>{
    input.addEventListener('keydown', function(e) {
    if (e.keyCode === 13) {
        if ( inpPopupTitle.value) {
            frmAddPost.submit();
            addPopupPost.classList.remove('popup_opened')
        }
    }}
    )
})
