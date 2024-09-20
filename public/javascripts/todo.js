function deleteTodoAjax(todoId) {
    const form = document.getElementById(`delete-form-${todoId}`);
    const todoItem = document.getElementById(`todo-${todoId}`);

    console.log(todoItem);

    if (form && todoItem) {
        // CSRFトークンを取得
        const csrfToken = form.querySelector('input[name=csrfToken]').value;

        // 非同期リクエストを送信
        fetch(form.action, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Csrf-Token': csrfToken
            },
        }).then(response => {
            if (response.ok) {
                // 成功したらToDoアイテムをページから削除
                todoItem.remove();
                console.log('削除成功');
            } else {
                console.error('削除に失敗しました。ステータス:', response.status);
            }
        }).catch(error => {
            console.error('エラーが発生しました:', error);
        });
    } else {
        console.error('フォームまたはToDoアイテムが見つかりません');
    }
}
