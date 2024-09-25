document.querySelectorAll('.todo-ele').forEach(item => {
    item.addEventListener('click', function () {
        // 他の要素がアクティブの場合、非表示にする
        document.querySelectorAll('.todo-ele.active').forEach(activeItem => {
            if (activeItem !== item) {
                activeItem.classList.remove('active');
            }
        });

        // クリックした要素のポップアップメニューをトグルする
        item.classList.toggle('active');
    });
});

// ポップアップ外をクリックしたら閉じる
window.addEventListener('click', function (event) {
    if (!event.target.closest('.todo-ele')) {
        document.querySelectorAll('.todo-ele.active').forEach(activeItem => {
            activeItem.classList.remove('active');
        });
    }
});