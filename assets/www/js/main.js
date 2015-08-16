var data;
var currentItem;
var backItem;
function load() {
	//给组件添加事件监听
	addListener();
	//获取文件系统相关的数据 
	loadFiles("/mnt/sdcard/");
}

function loadFiles(path){
	//调用Android代码 获取文件目录结构
	var jsonary=window.explore.loadFiles(path);
	//把jsonary转成js对象
	data=eval("("+jsonary+")");
	//获取ul
	var ul=document.getElementById("list");
	//先把ul清空  然后添加
	ul.innerHTML="";
	for(i=0; i<data.length; i++){
		var file=data[i];
		var li=document.createElement("li");
		var left=document.createElement("div");
		left.className="icon";
		//判断是否是一个文件  
		//如果是则根据文件的后缀名设置背景图片
		if(!file.isDir){
			var imagePath=getImagePath(file.text);	
			left.style.backgroundImage="url(img/"+imagePath+")";
		}
		var center=document.createElement("div");
		center.className="text";
		//如果text的字数太长  则最后字符显示....
		var text=file.text;
		if(text.length>15){
			text=text.substr(0,15)+"....";
		}
		center.innerHTML=text;
		var right=document.createElement("div");
		//判断是否是文件  如果是文件则不显示
		if(file.isDir){
			right.className="pointer";
		}
		//组装
		li.appendChild(left);
		li.appendChild(center);
		li.appendChild(right);
		//为了点击的时候获取index  所以再追加一个span
		var span=document.createElement("span");
		span.innerHTML=i;
		li.appendChild(span);
		//给li绑定事件监听
		li.addEventListener("touchstart", startTouch);
		li.addEventListener("touchend", endTouch);
		li.addEventListener("touchmove", moveTouch);
		ul.appendChild(li);
	}
	
}

function addListener () {
	var cancel=document.getElementById("liCancel");
	cancel.addEventListener("touchend", function () {
		//隐藏ol
		var ol=document.getElementById("operation");
		ol.style.display="none";
	});
	
	var deleteL=document.getElementById("deleteF");
	deleteL.addEventListener("touchend", function () {
		//删除文件
		window.explore.deleteFile(currentItem);
		var ol=document.getElementById("operation");
		ol.style.display="none";
		loadFiles(backItem);
	});
	
	var renameF=document.getElementById("rename");
	renameF.addEventListener("touchend", function () {
		//重命名
		var i = window.explore.rename(currentItem);
		var ol=document.getElementById("operation");
		ol.style.display="none";
		loadFiles(backItem);
	});	
}


function endTouch () {
	//修改li的background 
	this.style.backgroundColor="white";
	this.style.color="black";
	//判断用户是否是取消后触发的endTouch
	if(isCancel){
		return;		
	}
	//获取当前选中的是文件夹还是文件 
	//如是是文件：
	//获取当前选中的item的position
	var spans=this.getElementsByTagName("span");
	var index=spans[0].innerHTML;
	//index就是当前选中项的下标位置 
	var file=data[index];
	if(!file.isDir){
		//显示操作栏
		var ol=document.getElementById("operation");
		ol.style.display="block";
		var header=document.getElementById("header");
		backItem=header.innerHTML;
		currentItem = backItem+file.text;		
	}else{
		//进入文件夹
		//文件夹的目标绝对路径
		var header=document.getElementById("header");
		var currentPath=header.innerHTML;
		var targetPath=currentPath+file.text+"/";
		loadFiles(targetPath);
		//更新header中的路径
		header.innerHTML=targetPath;
	}
}

function startTouch () {
	//修改li的background 
	this.style.backgroundColor="#dddddd";
	this.style.color="white";
	isCancel=false;
}

var isCancel=true;
function moveTouch () {
	//修改li的background 
	this.style.backgroundColor="white";
	this.style.color="black";
	isCancel=true;
}


//根据文件名称的后缀  判断到底使用什么文件
function getImagePath (text) {   //xxxx.txt
	var lastIndex=text.lastIndexOf(".");
	if(lastIndex<0){
		return "unknown.png";
	}
	var laststr=text.substr(lastIndex);
	if(laststr==".txt"){
		return "txt.png";
	}else if(laststr==".png"){
		return "png.png";
	}else if(laststr==".tif"){
		return "tif.png";
	}else if(laststr==".avi"){
		return "txt.png";
	}else if(laststr==".doc"){
		return "doc.png";
	}else if(laststr==".mov"){
		return "mov.png";
	}else if(laststr==".jpg"){
		return "jpg.png";
	}else if(laststr==".rar"){
		return "rar.png";
	}else if(laststr==".ppt"){
		return "ppt.png";
	}else if(laststr==".pdf"){
		return "pdf.png";
	}else if(laststr==".mp3"){
		return "mp3.png";
	}else if(laststr==".mov"){
		return "mov.png";
	}else if(laststr==".zip"){
		return "zip.png";
	}else if(laststr==".wav"){
		return "wav.png";
	}
	return "unknown.png";
}

/** 回退 */
function goBack(){
	//把当前路径获取
	var header=document.getElementById("header");
	var currentPath=header.innerHTML;
	//截取最后一部分
	if(currentPath=="/"){
		//推出当前的activity    调用java代码
		window.explore.finishActivity();
	}
	//  /mnt/sdcard/  截取0~倒数第二个/的部分
	var subPath=currentPath.substr(0, currentPath.length-1);
	//  /mnt/sdcard
	var targetPath=subPath.substr(0, subPath.lastIndexOf("/")+1);
	//再次重写加载文件目录
	loadFiles(targetPath);
	//给header重新设置innerHTML
	header.innerHTML=targetPath;
}


