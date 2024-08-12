let currentUserId = '[[${currentUser.id}]]';

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
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                                    <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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
                            html += `<p> => ${comment.comContent}</p>
                                        <div class="btnWrapper">`;

                            if (stringUserNo === userNo) {
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                                            <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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
                            html += `<p> => ${comment.comContent}</p>
                        <div class="btnWrapper">`;

                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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
    const idx = $(".re-reply-save").index(event.target);
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
                            html += `<div class="contentDiv gray3-border">${comment.comContent}</div>
                        <div class="btnWrapper">`;

                            // 사용자 ID가 일치할 경우 수정 및 삭제 버튼 추가
                            if (stringUserNo === currentUserId) {
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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
                                html += `<p class="text-body-secondary modified pBtn gray6">수정</p>
                            <p class="text-body-secondary deleteP pBtn gray6">삭제</p>`;
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