<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>찾아줘 왓슨!</title>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
        <script src="http://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
		<link rel="stylesheet" href="../resources/css/mainStyle.css">
    </head>
    <body>
<div class="container col-12">
        <jsp:include page="../standard/header.jsp" />
            <!--            -->
	<div class="row mt-2">
	    <div class="col d-none d-sm-block"></div>
<div class="col-12 text-center mt-5">
    <div class=row>
        <div class="col-12">
            결제 실패!
    관리자에게 문의바람
    admin@admin.co.kr
        </div>
    </div>
    <div class="row mt-2 mb-2">
        <div class="col-12 text-center">
            <button id="toMainBtn" type="button" class="btn btn-outline-secondary">돌아가기</button>
        </div>
    </div>
</div>

	    <div class="col d-none d-sm-block"></div>
	</div>
	   <!--            -->
            <jsp:include page="../standard/footer.jsp" />
</div>
        
        <script>
            
            //나중에 링크 수정하기
        $("#myDonaBtn").on("click", function(){
            location.href="#";
        })
        $("#toMainBtn").on("click", function(){
            location.href="${pageContext.request.contextPath}/toMain.main";
        })
        </script>
    </body>
</html>