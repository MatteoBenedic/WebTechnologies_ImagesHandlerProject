/**
 * 
 */
{
	let myAlbumList, otherAlbumList, createAlbum, uploadImage,
	imagesAlbum, orderImages, imageDetails, currentAlbum, currentImage,
	pageController = new PageController();
	
	
	window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageController.start(); 
	      pageController.refresh();
	    } 
	  }, false);
	  
	
	function CreateAlbum(alert, button, form){
		this.alert = alert;
		this.button = button;
		this.form = form;
		
		this.registerEvents = function(){
			var self = this;
			button.addEventListener('click', (e) => {
			if (form.checkValidity()) {
				makeCall("POST", 'CreateAlbum', form,
				function(x) {
          			if (x.readyState == XMLHttpRequest.DONE) {
            			var message = x.responseText;
            			switch (x.status) {
              				case 200:
            					myAlbumList.show();
                			break;
              				case 400: // bad request
                				self.alert.textContent = message;
                			break;
              				case 401: // unauthorized
              					self.alert.textContent = message;
              					if (sessionStorage.getItem("username") == null) {
	     							window.location.href = "index.html";
	     						}
                  			break;
              				case 500: // server error
            					self.alert.textContent = message;
                			break;
            			}
          			}
        		}, false);
    		} else {
    	 		form.reportValidity();
    		}
		});
		};
		
		this.reset= function(){
			this.block.style.visibility = "hidden";
		};
		
		this.show = function(){
			this.block.style.visibility = "visible";
		};
	};
	
	
	function UploadImage(alert, block, button, choice){
		this.alert = alert;
		this.button = button;
		this.block = block;
		this.choice = choice;
		
		this.registerEvents = function(){
			var self = this;
			button.addEventListener('click', (e) => {
			var form = e.target.closest("form"); 
			if (form.checkValidity()) {
				makeCall("POST", 'UploadImage', e.target.closest("form"),
				function(x) {
          			if (x.readyState == XMLHttpRequest.DONE) {
            			var message = x.responseText;
            			switch (x.status) {
              				case 200:
            					myAlbumList.autoclick(currentAlbum);
                			break;
              				case 400: // bad request
                				self.alert.textContent = message;
                			break;
              				case 401: // unauthorized
              					self.alert.textContent = message;
              					if (window.sessionStorage.getItem("username").valueOf == null) {
	     							window.location.href = "index.html";
	     						}
                  			break;
              				case 500: // server error
            					self.alert.textContent = message;
                			break;
                			default:
								self.alert.textContent = message;
            			}
          			}
        		}
      			);
    		} else {
    	 		form.reportValidity();
    		}
		});
		};
	
	
		this.reset= function(){
			block.style.visibility = "hidden";
			while (choice.firstChild) {
               	 choice.removeChild(this.choice.firstChild); 
           		}
		};
		
		this.show = function(){
			block.style.visibility = "visible";
		};
		
		this.update = function(id, name){
			box = document.createElement("input");
			box.type = "checkbox";
			box.id = id;
			box.name = "albums";
			box.value = id;
			choice.appendChild(box);
			lbl = document.createElement("label");
			lbl.htmlFor = id;
			lbl.textContent = name + "  ";
			choice.appendChild(lbl);
		};
	};
	
	
	
	function AlbumList(alert, call, block, body, personal){
		this.alert = alert;
	    this.listcontainer = block;
	    this.listcontainerbody = body;
	    this.call = call;
	    this.personal = personal;

	    this.reset = function() {
	      this.listcontainer.style.visibility = "hidden";
	      this.mine = false;
	    };

	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", call, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var albumsToShow = JSON.parse(req.responseText);
	              if (albumsToShow.length == 0) 
	                return;
	              
	              self.update(albumsToShow); 
	              if (next) next(); 
	            
	          } else if (req.status == 401) {
                  window.location.href = "index.html";
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };

		
	    this.update = function(arrayAlbums) {
	      var row, destcell, datecell, linkcell, anchor;
	      this.listcontainerbody.innerHTML = ""; 
	      if(personal)
	      	uploadImage.reset(); 
	      var self = this;
	      arrayAlbums.forEach(function(album) { 
	        row = document.createElement("tr");
	        destcell = document.createElement("td");
	        destcell.textContent = album.id;
	        row.appendChild(destcell);
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        anchor.className = "blocklink";
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(album.title);
	        anchor.appendChild(linkText);
	        anchor.setAttribute('albumid', album.id); 
	        anchor.addEventListener("click", (e) => {
			  currentAlbum = e.target.getAttribute("albumid");
			  imagesAlbum.reset();
	          imagesAlbum.show(currentAlbum, self.personal); 
	        }, false);
	   		
	        row.appendChild(linkcell);
	        datecell = document.createElement("td");
	        datecell.textContent = album.creationdate;
	        row.appendChild(datecell);
	        
	        if(personal){
	        	self.mine = true;
	        	uploadImage.update(album.id, album.title);
			}else{
				destcell = document.createElement("td");
	        	destcell.textContent = album.creator;
	        	row.appendChild(destcell);
			}
			
        	
	        self.listcontainerbody.appendChild(row);
	      });
	      
	      if(this.mine){
	      	uploadImage.show();
	      	}
	      	
	      this.listcontainer.style.visibility = "visible";

	    };

	    this.autoclick = function(albumId) {
	      var e = new Event("click");
	      var selector = "a[albumid='" + albumId + "']";
	      var anchorToClick =  
	        (albumId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    };

	}
	
	
	function ImagesAlbum(alert, messIm, prev, next, imgcellsarr, allContainer, ordButton){
		this.alert = alert;
		this.messIm = messIm;
		this.prev = prev;
		this.next = next;
		this.imgcells = imgcellsarr;
		this.allContainer = allContainer;
		this.orderButton = ordButton;
		
		
		this.registerEvents = function(){
			var button = this.orderButton.querySelector('button');
			button.addEventListener("click", (e) => {
				orderImages.reset();
				orderImages.show(this.images);
			});
		};
		
		this.reset = function(){
			this.alert.textContent = "";
			this.messIm.textContent = "";
			this.orderButton.style.visibility = "hidden";
			var lim = this.imgcells.length;
			for(let i = 0; i < lim; i++){
				var el = this.imgcells[i];
				while (el.firstChild) {
               	 el.removeChild(el.firstChild); 
           		}
			}
			orderImages.reset();
            this.allContainer.style.visibility = "hidden";
		};
		
		
		this.show = function(id, personal){
			var self = this;
	      makeCall("GET", "GetAlbumImages?albumid=" + id, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var imagesToShow = JSON.parse(req.responseText);
	              self.reset();
	              if (imagesToShow.length == 0){
					  self.alert.textContent = "No images yet!";
					  self.update(imagesToShow, 0, personal);
	                return;
	              }
	              self.update(imagesToShow, 0, personal); 
	          } else if (req.status == 403) {
                  window.location.href = "index.html";
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
		};
		
		this.update = function(imagesToShow, page, personal){
			this.personal = personal;
			var self = this;
			this.images = imagesToShow;
			var limit = imagesToShow.length;
			var pageLimit = (page*5) + 5;
			if(page !== 0){
				this.prev.style.visibility = "visible";
				var button = prev.querySelector('button');
				button.addEventListener("click", (e) => {
					self.reset();
					self.update(imagesToShow, page-1);
				}, false);
				}else{
					this.prev.style.visibility = "hidden";
				}
			if(limit > pageLimit){
				this.next.style.visibility = "visible";
				var button = next.querySelector("button");
				button.addEventListener("click", (e) => {
					self.reset();
					self.update(imagesToShow, page+1);
				}, false);
				}else{
					this.next.style.visibility = "hidden";
				}
			for(let i = page*5; i< limit && i<pageLimit; i++){
				
				var cell = self.imgcells[i - page*5];
				var imageInfo = imagesToShow[i];
        		var pTag = document.createElement("div");
        		pTag.textContent= imageInfo.title;
        		cell.appendChild(pTag);
        		var br = document.createElement("br");
        		cell.appendChild(br);
        		var img = document.createElement("img");
        		img.src = imageInfo.filepath;
        		img.width = 100;
        		img.height = 100;
        		
        		
        		img.addEventListener("mouseover", (e) => {
					var tag = e.target.nextElementSibling;
					currentImage = self.images[tag.textContent];
					imageDetails.close();
					imageDetails.reset();
					imageDetails.show(currentImage, this.personal);
				}, false);
				
				cell.appendChild(img);
				
				var iTag = document.createElement("span");
        		iTag.style.display = "none";
        		iTag.textContent = i;
        		cell.appendChild(iTag);
			}
			
			if(limit > 0){
				if(personal)
					this.orderButton.style.visibility = "visible"; 
	
				this.allContainer.style.visibility = "visible";
			}
		};
	}
	
	
	function OrderImages(alert, ordTable, ordTitles, savOrdButton){
		this.alert = alert;
		this.ordTable = ordTable;
		this.ordTitles = ordTitles;
		this.savOrdButton = savOrdButton;
		
		this.registerEvents = function(){
			savOrdButton.addEventListener("click", (e) => {
				var newOrder = [];
        		this.ordTitles.querySelectorAll('p').forEach((p) => {
            		newOrder.push(parseInt(p.id));
        		});	
        		
        		var dataToSend = {
  				  	"albumId": currentAlbum,
				    "ids": newOrder
				};
				var jsonPayload = JSON.stringify(dataToSend);
				
        		var req = new XMLHttpRequest();
				req.open('POST', 'SetOrder', true);
				req.setRequestHeader('Content-Type', 'application/json');
				
				var selff = self;
				req.onreadystatechange = function () {
    				if (req.readyState === XMLHttpRequest.DONE) {
        			if (req.status === 200) {
            	// Success: process response
            			imagesAlbum.reset();
            			imagesAlbum.show(currentAlbum, true);
        			} else {
            	// Error: handle the error
            		selff.alert.textContent = req.responseText;
       			}}
				};

				req.send(jsonPayload);
				});
		};
			
		this.reset = function() {
        	this.ordTitles.innerHTML = "";
        	while (this.ordTitles.firstChild) {
            	this.ordTitles.removeChild(this.ordTitles.firstChild);
        	}
        	this.ordTable.style.visibility = "hidden";
    	};

    	this.show = function(images) {
        	this.imlist = images;
        	var self = this;
        	for (let i = 0; i < this.imlist.length; i++) {
            	var cell = document.createElement('td');
            	var p = document.createElement('p');

            	p.textContent = images[i].title;
            	p.draggable = true;
            	p.dataset.index = i;
            	p.id = images[i].id;

            	cell.appendChild(p);
            	this.ordTitles.appendChild(cell);
        	}

        	let draggedElement = null;

        	this.ordTitles.addEventListener('dragstart', (e) => {
            	if (e.target.tagName === 'P') {
                	draggedElement = e.target.parentElement;
                	e.dataTransfer.effectAllowed = 'move';
                	e.dataTransfer.setData('text/html', draggedElement.outerHTML);
            	}
        	});

        	this.ordTitles.addEventListener('dragover', (e) => {
            	if (draggedElement) {
                	e.preventDefault();
                	e.dataTransfer.dropEffect = 'move';
            	}
        	});

        	this.ordTitles.addEventListener('drop', (e) => {
            	if (draggedElement) {
                	e.preventDefault();
               		if (e.target.tagName === 'TD' || e.target.tagName === 'P') {
                    	let target = e.target.tagName === 'P' ? e.target.parentElement : e.target;
                    	if (target && target !== draggedElement) {
                        
                        	let fromIndex = parseInt(draggedElement.querySelector('p').dataset.index);
                        	let toIndex = parseInt(target.querySelector('p').dataset.index);

                        
                           	if (fromIndex < toIndex) {
                            	this.ordTitles.insertBefore(draggedElement, target.nextSibling);
                       		} else {
                            
                            	this.ordTitles.insertBefore(draggedElement, target);
                        	}
                        
                        
                        	self.updateIndices();
                    	}
                	}
            	}
        	});

        	this.ordTitles.addEventListener('dragend', (e) => {
            	e.preventDefault();
            	draggedElement = null;
        	});

        	this.ordTable.style.visibility = "visible";
    	};

    	this.updateIndices = function() {
        	const cells = this.ordTitles.querySelectorAll('td');
        	cells.forEach((cell, index) => {
            	const p = cell.querySelector('p');
            	p.dataset.index = index;
        	});
    	};
		
		
	};
	
	
	
	function ImageDetails(alert, modalContainer, infoImage, commentsList, deleteButton, commentButton, commentForm, closeButton/*, inputid*/){
		this.alert = alert;
		this.modalContainer = modalContainer;
		this.dataImage = infoImage;
		this.commentsList = commentsList;
		this.deleteButton = deleteButton;
		this.commentButton = commentButton;
		this.commentForm = commentForm;
		this.closeButton = closeButton;
	
		
		this.registerEvents = function(){
			var self = this;
			
			deleteButton.addEventListener('click', (e) => {
				makeCall("POST", 'DeleteImage?id=' + currentImage.id, null,
				function(x) {
          			if (x.readyState == XMLHttpRequest.DONE) {
            			var message = x.responseText;
            			switch (x.status) {
              				case 200:
            					self.close();
            					myAlbumList.autoclick(currentAlbum);
                			break;
              				case 400: // bad request
                				self.alert.textContent = message;
                			break;
              				case 401: // unauthorized
              					if (sessionStorage.getItem("username") == null) {
	     							window.location.href = "index.html";
	     						}
                  			break;
              				case 500: // server error
            					self.alert.textContent = message;
                			break;
            			}
          			}
        		}, false);
			});
			
			closeButton.addEventListener('click', (e) => {
				imageDetails.close();
    		
			}, false);
		
			commentButton.addEventListener("click", (e) => {
			form = e.target.closest("form");
			if (form.checkValidity()) {
				var text = document.getElementsByName("comment")[0].value;
				
				if(text.trim() !== ""){
				makeCall("POST", 'CreateComment?imageid=' + currentImage.id, commentForm,
				function(x) {
          			if (x.readyState == XMLHttpRequest.DONE) {
            			var message = x.responseText;
            			switch (x.status) {
              				case 200:
								self.reset();
            					self.close();
            					self.show(currentImage);
                			break;
              				case 400: // bad request
                				self.alert.textContent = message;
                			break;
              				case 401: // unauthorized
              					self.alert.textContent = message;
              					if (sessionStorage.getItem("username") == null) {
	     							window.location.href = "index.html";
	     						}
                  			break;
              				case 500: // server error
            					self.alert.textContent = message;
                			break;
            			}
          			}
        		}
      			);
      			}else{
					if (form !== null) {
	     			 form.reset();
	    		  	}
				  	self.alert.textContent = "Invalid comment text";
				}
    		} else {
    	 		form.reportValidity();
    		}	
			});
		};
		
		this.show = function(imageInfo, personal){
			var image = imageInfo;
			var self = this;
	      makeCall("GET", "GetImageComments?imageid=" + image.id, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var commentsToShow = JSON.parse(req.responseText);
	              self.reset();
	              
	              self.update(imageInfo, commentsToShow, personal); 
	          } else if (req.status == 403) {
                  window.location.href = "index.html";
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
		};
		
		this.close = function(){
			this.modalContainer.style.display = "none";
		};
		
		this.reset = function(){
			this.commentsList.style.visibility = "hidden";
		 	this.dataImage.style.visibility = "hidden";
		  	this.commentForm.style.visibility = "hidden";
		  	this.deleteButton.style.visibility = "hidden";
		
		}
		
		this.update = function(imageInfo, commentsToShow, personal){
	      var imgTag = this.dataImage.querySelector("img");
	      var pTag = this.dataImage.querySelector("p");
	      var hTag = this.dataImage.querySelector("h3");
	      
	      if (pTag) {
			  pTag.textContent = "";
			  pTag.textContent = imageInfo.title;
          }	
        	
          if(hTag){
			  hTag.textContent = "";
			  hTag.textContent = imageInfo.description;
		  }

          if (imgTag) {
			  imgTag.removeAttribute("src");
			  imgTag.src = imageInfo.filepath;
		  }
          
     
          
	      var row, destcell;
	      this.commentsList.innerHTML = ""; 
	     
	      var self = this;
	      commentsToShow.forEach(function(comment) { 
	        row = document.createElement("tr");
	        destcell = document.createElement("td");
	        destcell.textContent = comment.creator;
	        row.appendChild(destcell);
	        destcell = document.createElement("td");
	        destcell.textContent = comment.text;
	        row.appendChild(destcell);
	        
	        self.commentsList.appendChild(row);
	      });
	      
	      if(personal){
				self.deleteButton.style.visibility = "visible";
			}
			
		  this.commentsList.style.visibility = "visible";
		  this.dataImage.style.visibility = "visible";
		  this.commentForm.style.visibility = "visible";
		  this.modalContainer.style.display = "block";
		};
		
	}
	
	
	
	
	
	
	
	function PageController() {
		    var alertContainer = document.getElementById("id_alert");
		    var imgcellsarr = document.querySelectorAll(".imgcell");
		    
		    this.start = function() {
		      
		      createAlbum = new CreateAlbum(
				  alertContainer,
				  document.getElementById("albumbutton"),
				  document.getElementById("createAlbum"));
		      createAlbum.registerEvents();
		      
		      
		      myAlbumList = new AlbumList(
				  alertContainer,
				  "GetPersonalAlbumsData",
				  document.getElementById("id_myAlbums"),
				  document.getElementById("id_listPersonalAlbums"),
				  true
				);
	
		      otherAlbumList = new AlbumList(
				  alertContainer,
				  "GetOthersAlbumsData",
				  document.getElementById("otherAlbums"),
				  document.getElementById("id_listOtherAlbums"),
				  false
			  );
		      
		      uploadImage = new UploadImage(
				  alertContainer,
				  document.getElementById("id_uploadImage"),
				  document.getElementById("upimagebutton"),
				  document.getElementById("id_albumChoice")
			  );
		      uploadImage.registerEvents();
		      
		      imagesAlbum = new ImagesAlbum(
				  alertContainer,
				  document.getElementById("messIm"),
				  document.getElementById("id_prevImages"),
				  document.getElementById("id_nextImages"),
				  imgcellsarr,
				  document.getElementById("id_imagesAlbumContainer"),
				  document.getElementById("askorderbutton")
			  );
			  imagesAlbum.registerEvents();
			  
			  orderImages = new OrderImages(
				  alertContainer,
				  document.getElementById("orderTable"),
				  document.getElementById("ordertitles"),
				  document.getElementById("saveOrderButton")
			  );
			  orderImages.registerEvents();
		      
		      imageDetails = new ImageDetails(
				  alertContainer,
				  document.getElementById("myModal"),
				  document.getElementById("imagezoom"),
				  document.getElementById("commentsList"),
				  document.getElementById("deleteImageButton"),
				  document.getElementById("commentbutton"),
				  document.getElementById("commentForm"),
				  document.getElementById("closebutton")
			  );
			  imageDetails.registerEvents();
		     
		      document.querySelector("a[href='']").addEventListener('click', () => {
                window.sessionStorage.removeItem('username');
                window.location.href = "index.html";
                makeCall("GET", 'Logout', form,
	        	function(x) {
	          	if (x.readyState == XMLHttpRequest.DONE) {
	            	var message = x.responseText;
	            	switch (x.status) {
	            	  	case 200: 
	                		break;
	              		default: // bad request
	                		alertContainer.textContent = message;
	                		break;
	            	}
	          	}
	        	}, false);
		      });
		    };  
		    
		    this.refresh = function() { 
	      		alertContainer.textContent = "";    
	      		otherAlbumList.reset();   
	      		myAlbumList.reset();
	      		imageDetails.reset();
	      		imagesAlbum.reset();
	      		uploadImage.reset();
	      		otherAlbumList.show();
	     		myAlbumList.show();	
	      		orderImages.reset();
	   		 };
		}
}