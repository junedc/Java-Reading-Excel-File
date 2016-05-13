<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
</head>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>

<script type="text/javascript">



	   $(function (){
	 		$("#btnImportExcel").click(function(){	 			
	 		    $('form[id=formImport]').iframePostForm ({
	 		        post : function (){
	 		    	var form = document.getElementById("formImport");
					form.enctype="multipart/form-data" ;
					form.encoding="multipart/form-data" ;
					form.action="openexcel";
				
	 		        },
	 		        complete : function (data){	
	 		        	
		 		        	displayExcelData(data);
		 		       
	 		        }
	 		    });
	 		});
	 	});

	 	function displayExcelData(excelData) {
               
			alert(excelData);
	 	}
</script>
<body>
<h1>Confirmed working with max validation.  Check if mapping is complete. sometimes its not complete thats why its causing errors.  read the help.txt</h1>
<form id="formImport" method="post" action="openexcel" enctype="multipart/form-data">
         <c:if test="${not empty errors}">
            <h2 style="color:red;">${errors}.</h2>
        </c:if>
<input type="file" name="uploadedFile" size="40">
<button id="btnImportExcel">Import Excel</button>

</form>
</body>
</html>
