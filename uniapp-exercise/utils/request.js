const BASE_URL = `http://localhost:9090`

export const request = (options) => {  // {url: '', method: '', data: {}}
	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			header: {token: uni.getStorageSync('user') ? uni.getStorageSync('user').token : ''},
			data: options.data || {},
			success: (res) => {
				const data = res.data
				if (data.code === '401') {  // 401表示无权限
					uni.navigateTo({
						url: '/pages/login/login'
					})
					return
				} else if(data.code !== '200') {
					uni.showToast({
						icon: 'error',
						title: '操作错误'
					})
				}
				resolve(data) 
			},
			fail: (error) => {
				uni.showToast({
					icon: 'error',
					title: '系统错误'
				})
				reject(error)
			}
		})
	})
}