const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
    devServer: {
            proxy: {
                '/iot-api': {
                    target: 'https://iot-api.heclouds.com',
                    changeOrigin: true,
                    pathRewrite: {
                        '^/iot-api': ''
                    }
                },
                '/user': {
                      target: 'http://localhost:8181',
                      changeOrigin: true,
                      pathRewrite: {
                          '^/user': ''
                      }
                  },
            }
        }
})
