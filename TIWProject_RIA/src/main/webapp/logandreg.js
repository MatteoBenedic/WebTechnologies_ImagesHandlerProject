/**
 * Login and registration
 */

(function() { 

  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'Login', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "uniquepage.html";
                break;
              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });
  
  
  document.getElementById("registrationbutton").addEventListener("click", (e) => {
	var form = e.target.closest("form");
    if (form.checkValidity()) {
		var usrn = document.getElementsByName("usrn")[0].value;
		var mail = document.getElementsByName("mail")[0].value;
		var pwd = document.getElementsByName("passwd")[0].value;
		var repPwd = document.getElementsByName("checkpwd")[0].value;
		
		var valMail = function validateEmail(email) {
  			const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  
  			return emailRegex.test(email);
		}(mail);

		if(pwd === repPwd && (pwd.trim() !== "")){
			if(valMail){
	      	makeCall("POST", 'SubmitRegistration', form,
	        	function(x) {
	          	if (x.readyState == XMLHttpRequest.DONE) {
	            	var message = x.responseText;
	            	switch (x.status) {
	            	  	case 200: // correct registation
	            			document.getElementById("RegMessage").textContent = message;
	                		break;
	              		case 400: // bad request
	                		document.getElementById("errorRegMessage").textContent = message;
	                		break;
	              		case 401: // unauthorized
	                  		document.getElementById("errorRegMessage").textContent = message;
	                  		break;
	              		case 500: // server error
	            			document.getElementById("errorRegMessage").textContent = message;
	                		break;
	            	}
	          	}
	        	}
	      		);
	      	}else{
				  if (form !== null) {
	     			 form.reset();
	    		  }
				  document.getElementById("errorRegMessage").textContent = "Invalid Mail";
			  }
	     }else{
			if (form !== null) {
	      		form.reset();
	    	}
			document.getElementById("errorRegMessage").textContent = "Password not repeated correctly or invalid(blank)";
		 }
    } else {
    	 form.reportValidity();
    }
  });

})();