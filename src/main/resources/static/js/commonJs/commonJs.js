//입력에서 빈값 혹은 빈값으로 줄만넘겼을때 체크하는거
function validateInput(inputValue) {
    let isBoolean = true;
    if (/^\s*$/.test(inputValue)) {
        isBoolean = false;
        return isBoolean; // 폼이 제출되지 않도록 함
    }
    return isBoolean;
}

function alert1(title){
    return Swal.fire({
        title: title
    });
}

function alert2(title,text){
    return Swal.fire({
        title: title,
        text: text
    });
}
function alert3(title,text,icon){
    return Swal.fire({
        title: title,
        text:text,
        icon:icon
    });
}