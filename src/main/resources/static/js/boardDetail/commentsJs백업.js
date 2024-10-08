$(document).ready(() => {
    console.log('READY currentUserId => ', currentUserId);


});

//수정버튼 눌렀을때
$(document).on('click','.modified',function(event){
    let commentId = event.target.dataset.id;
    modifyComment(commentId);
})

async function modifyComment(commentId) {
    console.log('수정 =>', commentId);

    // 수정 버튼을 확인 버튼으로 변경
    let modifiedButton = $(`[data-id="${commentId}"].modified`);
    modifiedButton.text('확인');
    modifiedButton.removeClass('modified').addClass('confirm');

    // 취소 버튼을 표시
    let canceledButton = $(`[data-id="${commentId}"].canceled`);
    canceledButton.css('display', 'inline-block'); // CSS에서 display: none;을 제거

    let deleteButton = $(`[data-id="${commentId}"].deleteP`);
    deleteButton.css('display','none');

    // textarea의 readonly 속성을 false로 변경
    let textarea = $(`[data-id="${commentId}"].textarea-Class`);
    textarea.prop('readonly', false);
    textarea.addClass('modifyArea');
    textarea.focus();

    let originalContent = textarea.val();
    textarea.data('originalContent', originalContent); // 기존 값을 data 속성에 저장
}

// 수정에서 확인 눌렀을 경우
$(document).on('click', '.confirm', function(event) {
    let commentId = event.target.dataset.id;
    confirmModification(commentId);
});

async function confirmModification(commentId) {
    let textarea = $(`[data-id="${commentId}"].textarea-Class`);
    let newContent = textarea.val();

    console.log('확인 =>', commentId, '새로운 내용:', newContent);

    // 서버로 수정된 내용을 전송하는 AJAX 요청
    try {
        let data = {
            commentId : commentId,
            content : newContent
        };
        await axios.post(`/comment/${commentId}/update`,
            JSON.stringify(data),{
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(data=>{
                console.log('data => ',data);
                console.log('data.data => ',data.data);
                textarea.prop('readonly', true);
                textarea.removeClass('modifyArea');

                let confirmButton = $(`[data-id="${commentId}"].confirm`);
                confirmButton.text('수정');
                confirmButton.removeClass('confirm').addClass('modified');

                let canceledButton = $(`[data-id="${commentId}"].canceled`);
                canceledButton.css('display', 'none');

                let deleteButton = $(`[data-id="${commentId}"].deleteP`);
                deleteButton.css('display', 'inline-block');

                alert1("댓글이 수정 되었습니다.");

                // 3초(3000ms) 후 페이지를 리로드
                setTimeout(function() {
                    location.reload();
                }, 3000);
            })
            .catch(error=>{
                console.error('수정 실패:', error);
                alert('수정 중 문제가 발생했습니다.');
            })
    } catch (error) {
        console.error('수정 실패:', error);
        alert('수정 중 문제가 발생했습니다.');
    }
}


//취소버튼 눌렀을때
$(document).on('click', '.canceled', function(event) {
    let commentId = event.target.dataset.id;
    cancelModification(commentId);
});


function cancelModification(commentId) {
    console.log('취소 =>', commentId);

    // 확인 버튼을 다시 수정 버튼으로 변경
    let confirmButton = $(`[data-id="${commentId}"].confirm`);
    confirmButton.text('수정');
    confirmButton.removeClass('confirm').addClass('modified');

    // 취소 버튼을 숨김
    let canceledButton = $(`[data-id="${commentId}"].canceled`);
    canceledButton.css('display', 'none');

    let deleteButton = $(`[data-id="${commentId}"].deleteP`);
    deleteButton.css('display','inline-block');


    // textarea의 readonly 속성을 다시 true로 변경
    let textarea = $(`[data-id="${commentId}"].textarea-Class`);
    textarea.prop('readonly', true);
    textarea.removeClass('modifyArea');

    // textarea의 내용을 원래 내용으로 복원
    let originalContent = textarea.data('originalContent');
    textarea.val(originalContent);
}


//삭제버튼 눌렀을때
$(document).on('click','.deleteP',function(event){
    let commentId = event.target.dataset.id;
    deleteComment(commentId);

})
async function deleteComment(commentId){
    console.info('여기는 delete Comment => ',commentId);
    try{
        await axios.get(`/comment/${commentId}/deleteComment`)
            .then(data=>{
                if(data.data ==='as'){
                    alert1('이미 삭제된 댓글입니다.');
                }else if(data.data ==='s' || data.data === 'ss'){
                    alert1('댓글이 삭제되었습니다');

                    setTimeout(function() {
                        location.reload();
                    }, 3000);
                }else{
                    alert1('댓글이 삭제되지않았습니다');
                }
            })
            .catch(error=>{
                console.log('error => ',error)
            })
    }catch(e){
        console.warn('error => ',e);
    }
}



//맨처음 댓글 입력창에서 작성눌렀을때
$(document).on('submit', '#CommentForm', function(event) {
    commentSubmit(event);
});
async function commentSubmit(event){
    event.preventDefault();

    const boardNo = $("#comInsertBoardId").val();
    const userNo = $("#comInsertId").val();
    let comContent = $("#reply-content").val();
    console.log('comContent =>', comContent);

    let contentsCheck = validateInput(comContent);
    if (!contentsCheck) {
        alert1("내용을 입력해주세요.");
        return;
    }

    const lines = comContent.split('\n');
    const processedLines = [];

    lines.forEach(function (line) {
        const trimmedLine = line.replace(/\s+/g, ' ').trim(); // 연속된 공백을 하나의 공백으로 대체하고 양쪽 공백 제거
        processedLines.push(trimmedLine);
    });

    comContent = processedLines.join('\n'); // 수정된 내용을 다시 합칩니다.

    try{
        let data = '';
        await axios.post(`/boards/${boardNo}/comments`, {
            boardNo: boardNo,
            userNo: userNo,
            comContent: comContent
        })
            .then(response =>{
                console.log('response => ',response);
                data = response.data;
            })
            .catch(error =>{
                console.log(' error =>',error.message);
            })
        console.log('data =>',data);
        if(data === 1){
            await axios.get(`/boards/${boardNo}/comments`)
                .then(success =>{
                    console.log('success =>',success)
                    let html ='';
                    success.data.forEach(comment =>{
                        html += `<div class="commentDiv gray4-border" style="margin-left: ${comment.depth * 10}px;">`;
                        let stringUserNo = String(comment.userNo);

                        if(comment.depth===0){
                            html += `<div class="contentDiv gray3-border">${comment.comContent}</div>
                                        <div class="btnWrapper">`;
                            // 사용자 ID가 일치할 경우 수정 및 삭제 버튼 추가
                            if (stringUserNo === userNo) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                                    <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }
                            // 댓글달기 버튼
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply">댓글달기</p>
                                </div>`; // btnWrapper 닫기


                            // de1Recomment 폼 추가
                            html += `<div class="card de1Recomment">
                                        <form class="de1RecommentForm">
                                            <div class="card-body">
                                                <input class="comMemberNo" type="hidden" value='currentUserId' />
                                                <input class="comParentNo" type="hidden" value="${comment.id}" />
                                                <input class="comDepth" type="hidden" value="${comment.depth}" />
                                                <input class="comBoardNo" type="hidden" value="${comment.boardNo}" />
                                                <input class="comRef" type="hidden" value="${comment.comRef}" />
                                                <textarea class="form-control re-reply-content"></textarea>
                                                <br>
                                                <button type="submit" class="btn btn-primary re-reply-save">등록</button>
                                            </div>
                                        </form>
                                    </div>`; // de1Recomment 닫기
                        }
                        //depth가 0이아닐때
                        else{
                            // depth가 0이 아닐 때
                            html += `<!-- <p> => ${comment.comContent}</p>-->
                                    <div class="contentDiv gray3-border">
                                    <textarea class="form-control textarea-Class" row="1"
                                          data-id="${comment.id}"  readonly="true">${comment.comContent}</textarea>
                                    </div>
                                        <div class="btnWrapper">`;

                            if (stringUserNo === userNo) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                                            <p class="text-body-secondary canceled pBtn gray6" data-id="${comment.id}">취소</p>
                                            <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }

                            // 댓글달기 버튼 및 de2Recomment 폼 추가
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply2">댓글달기</p>
                                    </div> <!-- btnWrapper 닫기 -->
                                    <div class="card de2Recomment">
                                        <form class="de2RecommentForm">
                                            <div class="card-body">
                                                <input class="comMemberNo2" type="hidden" value="currentUserId" />
                                                <input class="comParentNo2" type="hidden" value="${comment.id}" />
                                                <input class="comDepth2" type="hidden" value="${comment.depth}" />
                                                <input class="comBoardNo2" type="hidden" value="${comment.boardNo}" />
                                                <input class="comRef2" type="hidden" value="${comment.comRef}" />
                                                <textarea class="form-control re-reply-content2"></textarea>
                                                <br>
                                                <button type="submit" class="btn btn-primary re-reply-save2">등록</button>
                                            </div>
                                        </form>
                                    </div>`; // de2Recomment 닫기
                        }
                        html += `</div>`; // commentDiv 닫기
                    })//forEach끝
                    $(".originComment").html(html);
                    $("#reply-content").val('');
                })//axios끝
                .catch(e =>{
                    console.warn(e.message);
                })
        }
    }catch(e){
        console.info(" async 에러! :",e);
    }

}




$(document).on('click', '.btn-reply', function(event) {
    replyBtnClick(event);
});

function replyBtnClick(event){
    console.log('event => ',event);
    let thisText = event.target.innerText;
    // console.log('this text => ',thisText);

    if(thisText == '댓글달기' ){
        event.target.innerText = '취소';
    }else{
        event.target.innerText = '댓글달기';
    }
    let idx = $('.btn-reply').index(event.target);
    console.log('index11 => ', idx);

    $(".de1Recomment").eq(idx).toggle();
}

/**
 * 댓글에 댓글 달기
 */
$(document).on('click', '.re-reply-save', function(event) {
    reCommentSub(event);
});
async function reCommentSub(event){
    event.preventDefault();
    const idx = $(".re-reply-save").index(event.target);
    console.log('인덱스확인~~', idx);

    const userNo = $(".comMemberNo").eq(idx).val();
    //게시물 번호(boradNo)
    const boardNo = $(".comBoardNo").eq(idx).val();
    //부모의 댓글 번호
    const comParentNo = $(".comParentNo").eq(idx).val();
    //같은 댓글 그룹
    const depth = $(".comDepth").eq(idx).val();
    //댓글 깊이
    const comRef = $(".comRef").eq(idx).val();
    //게시물
    let reReplyContent = $(".re-reply-content").eq(idx).val();

    console.log('userNo => ',userNo);
    console.log('boardNo => ',boardNo);
    console.log('comParentNo => ',comParentNo);
    console.log('depth => ',depth);
    console.log('reReplyContent => ',reReplyContent);

    if (reReplyContent == '') {
        alert1('내용입력해주세요.');
        return false;
    }



    const originalValue = reReplyContent;
    const lines = originalValue.split('\n');
    const processedLines = [];
    lines.forEach(function(line) {
        const trimmedLine = line.replace(/\s+/g, ' ').trim(); // 연속된 공백을 하나의 공백으로 대체하고 양쪽 공백 제거
        processedLines.push(trimmedLine);
    });
    reReplyContent = processedLines.join('\n');

    try{
        let data = '';
        // "/boards/"+boardNo+"/reComments"
        await axios.post(`/boards/${boardNo}/reComments`,{
            userNo: userNo,
            boardNo: boardNo,
            comParentNo: comParentNo,
            comContent: reReplyContent,
            depth: depth,
            comRef: comRef
        })
            .then( response =>{
                data = response.data;
            })
            .catch( error =>{
                console.warn('error =>',error.message );
            })

        if(data===1){
            await axios.get("/boards/"+boardNo+"/comments",{
                boardNo : boardNo
            })
                .then(success =>{
                    let html = '';
                    success.data.forEach( comment =>{
                        html += `<div class="commentDiv gray4-border" style="margin-left: ${comment.depth * 10}px;">`;
                        let stringUserNo = String(comment.userNo);
                        console.group();
                        console.info('stringUserNo {} {} ',typeof stringUserNo, stringUserNo);
                        console.info('currentUserId {} {} ',typeof currentUserId, currentUserId);

                        console.groupEnd();
                        if (comment.depth === 0) {
                            html += `<div class="contentDiv gray3-border">${comment.comContent}</div>
                        <div class="btnWrapper">`;

                            // 사용자 ID가 일치할 경우 수정 및 삭제 버튼 추가
                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }

                            // 댓글달기 버튼
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply">댓글달기</p>
                        </div>`; // btnWrapper 닫기

                            // de1Recomment 폼 추가
                            html += `<div class="card de1Recomment">
                            <form class="de1RecommentForm">
                                <div class="card-body">
                                    <input class="comMemberNo" type="hidden" value='currentUserId' />
                                    <input class="comParentNo" type="hidden" value="${comment.id}" />
                                    <input class="comDepth" type="hidden" value="${comment.depth}" />
                                    <input class="comBoardNo" type="hidden" value="${comment.boardNo}" />
                                    <input class="comRef" type="hidden" value="${comment.comRef}" />
                                    <textarea class="form-control re-reply-content"></textarea>
                                    <br>
                                    <button type="submit" class="btn btn-primary re-reply-save">등록</button>
                                </div>
                            </form>
                        </div>`; // de1Recomment 닫기

                        } else {
                            // depth가 0이 아닐 때
                            html += `  <div class="contentDiv gray3-border">
                                        <textarea class="form-control textarea-Class" row="1"
                                          data-id="${comment.id}"  readonly="true">${comment.comContent}</textarea>
                                        </div>
                        <div class="btnWrapper">`;

                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                                        <p class="text-body-secondary canceled pBtn gray6" data-id="${comment.id}">취소</p>
                            <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }

                            // 댓글달기 버튼 및 de2Recomment 폼 추가
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply2">댓글달기</p>
                        </div> <!-- btnWrapper 닫기 -->
                        <div class="card de2Recomment">
                            <form class="de2RecommentForm">
                                <div class="card-body">
                                    <input class="comMemberNo2" type="hidden" value="currentUserId" />
                                    <input class="comParentNo2" type="hidden" value="${comment.id}" />
                                    <input class="comDepth2" type="hidden" value="${comment.depth}" />
                                    <input class="comBoardNo2" type="hidden" value="${comment.boardNo}" />
                                    <input class="comRef2" type="hidden" value="${comment.comRef}" />
                                    <textarea class="form-control re-reply-content2"></textarea>
                                    <br>
                                    <button type="submit" class="btn btn-primary re-reply-save2">등록</button>
                                </div>
                            </form>
                        </div>`; // de2Recomment 닫기
                        }

                        html += `</div>`; // commentDiv 닫기
                    })//forEach끝
                    $(".originComment").html(html);
                    $("#reply-content").val('');
                })
                .catch(error =>{
                    console.warn('error =>',error.message);
                })
        }

    }catch(e){
        console.warn('e => ',e.message);
    }
}


//대댓글에 댓글
$(document).on('click', '.btn-reply2', function(event) {
    replyBtnClick2(event);
});

function replyBtnClick2(event){
    console.log('event => ',event);
    let thisText = event.target.innerText;

    if(thisText == '댓글달기' ){
        event.target.innerText = '취소';
    }else{
        event.target.innerText = '댓글달기';
    }
    const idx = $('.btn-reply2').index(event.target);
    console.log('index22 => ', idx);

    $(".de2Recomment").eq(idx).toggle();
}

//댓글에 댓글에서 등록버튼 눌렀을때
$(document).on('click', '.re-reply-save2', function(event) {
    reCommentSub2(event);
});

async function reCommentSub2(event){
    event.preventDefault();
    const idx = $(".re-reply-save2").index(event.target);
    console.log('인덱스확인~~', idx);

    const userNo = $(".comMemberNo2").eq(idx).val();
    //게시물 번호(boradNo)
    const boardNo = $(".comBoardNo2").eq(idx).val();
    //부모의 댓글 번호
    const comParentNo = $(".comParentNo2").eq(idx).val();
    //같은 댓글 그룹
    const depth = $(".comDepth2").eq(idx).val();
    //댓글 깊이
    const comRef = $(".comRef2").eq(idx).val();
    //게시물
    let reReplyContent = $(".re-reply-content2").eq(idx).val();

    console.log('userNo => ',userNo);
    console.log('boardNo => ',boardNo);
    console.log('comParentNo => ',comParentNo);
    console.log('depth => ',depth);
    console.log('reReplyContent => ',reReplyContent);

    if (reReplyContent == '') {
        alert1('내용입력해주세요!!!!!!!!!!!!.');
        return false;
    }


    const originalValue = reReplyContent;
    const lines = originalValue.split('\n');
    const processedLines = [];
    lines.forEach(function(line) {
        const trimmedLine = line.replace(/\s+/g, ' ').trim(); // 연속된 공백을 하나의 공백으로 대체하고 양쪽 공백 제거
        processedLines.push(trimmedLine);
    });
    reReplyContent = processedLines.join('\n');

    try{
        let data = '';
        // "/boards/"+boardNo+"/reComments"
        await axios.post(`/boards/${boardNo}/reComments`,{
            userNo: userNo,
            boardNo: boardNo,
            comParentNo: comParentNo,
            comContent: reReplyContent,
            depth: depth,
            comRef: comRef
        })
            .then( response =>{
                data = response.data;
            })
            .catch( error =>{
                console.warn('error =>',error.message );
            })

        if(data===1){
            await axios.get("/boards/"+boardNo+"/comments",{
                boardNo : boardNo
            })
                .then(success =>{
                    let html = '';
                    success.data.forEach( comment =>{
                        html += `<div class="commentDiv gray4-border" style="margin-left: ${comment.depth * 10}px;">`;
                        let stringUserNo = String(comment.userNo);

                        if (comment.depth === 0) {
                            html += `
<!-- <div class="contentDiv gray3-border">${comment.comContent}</div> -->
                            <div class="contentDiv gray3-border">
                                <textarea class="form-control textarea-Class" row="1"
                                          data-id="${com.id}"  readonly="true">${comment.comContent}</textarea>
                            </div>
                        <div class="btnWrapper">`;

                            // 사용자 ID가 일치할 경우 수정 및 삭제 버튼 추가
                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                                         <p class="text-body-secondary canceled pBtn gray6" data-id="${comment.id}">취소</p>
                            <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }

                            // 댓글달기 버튼
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply">댓글달기</p>
                        </div>`; // btnWrapper 닫기

                            // de1Recomment 폼 추가
                            html += `<div class="card de1Recomment">
                            <form class="de1RecommentForm">
                                <div class="card-body">
                                    <input class="comMemberNo" type="hidden" value="currentUserId" />
                                    <input class="comParentNo" type="hidden" value="${comment.id}" />
                                    <input class="comDepth" type="hidden" value="${comment.depth}" />
                                    <input class="comBoardNo" type="hidden" value="${comment.boardNo}" />
                                    <input class="comRef" type="hidden" value="${comment.comRef}" />
                                    <textarea class="form-control re-reply-content"></textarea>
                                    <br>
                                    <button type="submit" class="btn btn-primary re-reply-save">등록</button>
                                </div>
                            </form>
                        </div>`; // de1Recomment 닫기

                        } else {
                            // depth가 0이 아닐 때
                            html += `<p> => ${comment.comContent}</p>
                        <div class="btnWrapper">`;

                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6" data-id="${comment.id}">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6" data-id="${comment.id}">삭제</p>`;
                            }

                            // 댓글달기 버튼 및 de2Recomment 폼 추가
                            html += `<p class="text-body-secondary pBtn gray6 reCommentBtn btn-reply2">댓글달기</p>
                        </div> <!-- btnWrapper 닫기 -->
                        <div class="card de2Recomment">
                            <form class="de2RecommentForm">
                                <div class="card-body">
                                    <input class="comMemberNo2" type="hidden" value="currentUserId" />
                                    <input class="comParentNo2" type="hidden" value="${comment.id}" />
                                    <input class="comDepth2" type="hidden" value="${comment.depth}" />
                                    <input class="comBoardNo2" type="hidden" value="${comment.boardNo}" />
                                    <input class="comRef2" type="hidden" value="${comment.comRef}" />
                                    <textarea class="form-control re-reply-content2"></textarea>
                                    <br>
                                    <button type="submit" class="btn btn-primary re-reply-save2">등록</button>
                                </div>
                            </form>
                        </div>`; // de2Recomment 닫기
                        }

                        html += `</div>`; // commentDiv 닫기
                    })//forEach끝
                    $(".originComment").html(html);
                    $("#reply-content").val('');
                })
                .catch(error =>{
                    console.warn('error =>',error.message);
                })
        }

    }catch(e){
        console.warn('e => ',e.message);
    }
}


//입력에서 빈값 혹은 빈값으로 줄만넘겼을때 체크하는거
function validateInput(inputValue) {
    let isBoolean = true;
    if (/^\s*$/.test(inputValue)) {
        isBoolean = false;
        return isBoolean; // 폼이 제출되지 않도록 함
    }
    return isBoolean;
}