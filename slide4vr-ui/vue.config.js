module.exports = {
    devServer: {
        proxy: {
            "^/items": {
                target: "http://localhost:3000",
                ws: false,
                pathRewrite: {
                    "^/items": "/items"
                }
            }
        }
    },
    pages: {
        index: {
            entry: 'src/main.js',
            title: 'My CRUD Apps',
        }
    }
}