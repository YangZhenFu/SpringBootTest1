$(document).ready(function(){
	
	$('.content .quyu .info .city h3').click(function(){
		$('.content .quyu .info .city h3').removeClass('aaaa');
		$(this).siblings('ul').children('li').eq(0).children('.con').css('display','block');
		$('.info ul').slideUp(200);
		if ($(this).siblings('ul').css('display') == 'none') {
			$(this).siblings('ul').slideDown(200);
			$(this).addClass('aaaa'); 
		} else {
			$(this).siblings('ul').slideUp(200);
		}
	})


	$('.info .city ul li .title').click(function(){
		$('.con').slideUp(200);
		$('.info .city ul li .title.minus').removeClass('minus');
		if ($(this).siblings('.con').css('display') == 'none') {
			$(this).siblings('.con').slideDown(200);
			$(this).addClass('minus');
			$(this).children('img').attr('src','/static/me/images/-.png');
		} else {
			$(this).siblings('.con').slideUp(200);
			$(this).removeClass('minus');
			$(this).children('img').attr('src','/static/me/images/+.png');
		}
	})

	$('.table .title h3').click(function(){
		$(this).addClass('current').siblings().removeClass('current');
		$('.table .inform').eq($(this).index()).addClass('active').siblings().removeClass('active');
	})


	$('.guanli h3').click(function(){
		$(this).addClass('choose').siblings().removeClass('choose');
		$('.guanli .yaoshi').eq($(this).index()-1).addClass('show').siblings().removeClass('show');
	})


	$('.guanli .yaoshi ul li').click(function(){
		$('.mask').show();
		$('.window').show();
	})


	$('.window img').click(function(){
		$('.mask').hide();
		$('.window').hide();
	})

	$('.con p').click(function(){
		$(this).addClass('acd').siblings('p').removeClass();
	})

	function index(){
		$('.red').css('opacity','0.1');  //默认值
		setTimeout(" $('.red').css('opacity','1')",100); //第一次闪烁
		setTimeout( "$('.red').css('opacity','0.1')",200); //第二次闪烁
	};
	window.setInterval(index,400); //让index 多久循环一次 
})