function toggleCommentBtn( e) {
    const frm = e.form;
    const liCmt = e.parentElement;
    const cmtText = liCmt.querySelector('.comment_text');
    if ( e.className == "comment__edit") {
        cmtText.removeAttribute('readonly');
        liCmt.querySelector('.comment__save').style.display = 'block';
    }
    else if ( e.className == "comment__delete") {
        frm.querySelector('.comment__list_form_method').value = 'delete';
        frm.submit();
    }
    else if ( e.className == "comment__save") {
        if ( cmtText.value != '') {
            frm.submit();
        }
    }
}

const addPopupPost = document.querySelector('.popup_type_add')
const btnEditPost = document.querySelector('.header__post-edit')
const btnPopupClose = document.querySelector('.popup__close')

const inpPopupTitle = document.querySelector('.popup__title_input')

const btnPopupSave = document.querySelector('.popup__save')
const frmAddPost = document.querySelector('.popup__form')
const inputsPopup = document.querySelectorAll('.popup__input')

const btnDeletePost = document.querySelector('.header__post_delete')
const frmDeletePost = document.querySelector('.header__post_delete_form')

// Форма редактирования поста

// Открываем форму
btnEditPost.addEventListener('click', (e) => {
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

// Удаляем пост
btnDeletePost.addEventListener('click', (e) => {
    e.preventDefault();
    frmDeletePost.submit();
})


// Комментарии
const btnAddComment=document.querySelector('.add__comment_button')
const inputAddComment = document.querySelector('.add__comment_text')
const frmAddComment = document.querySelector('.add__comment_form')

btnAddComment.addEventListener("click", (e)=>{
    e.preventDefault();
    if ( inputAddComment.value) {
        frmAddComment.submit();
    }
})
