$(document).ready(function(){
	$('.map .title h3').click(function(){
		$(this).addClass('current').siblings().removeClass('current');
		$('.map .info').eq($(this).index()).addClass('active').siblings().removeClass('active');
	})


	$('.info .camera img').hover(function(){
		$(this).siblings('.camera_info').show();
	},function(){
		$(this).siblings('.camera_info').hide();
	})

	var img_w = $('.map .info .ditu').width();
	var info_w = $('.map .info').width();
	// console.log(img_w)
	if (img_w > info_w) {
		img_w = info_w
	} 
})