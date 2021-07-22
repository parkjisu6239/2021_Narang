module.exports = {
  root: true,
  env: {
      browser: true
  },
  parserOptions: {
      parser: 'babel-eslint',
      sourceType: 'module'
  },
  extends: [
      'plugin:vue/vue3-essential',
      'eslint:recommended'
  ],
  plugins: [
      'import'
  ],
  rules: {
      'class-methods-use-this': 'off',
      'generator-star-spacing': 'off',
      'arrow-parens': 'off',
      'one-var': 'off',
      'prefer-promise-reject-errors': 'off',
      'quote-props': 'off',
      'prefer-const': 'off',
      'quotes': ['error', 'single'],
      'no-multiple-template-root': 'off',
      'import/first': 'off',
      'import/named': 'error',
      'import/namespace': 'error',
      'import/default': 'error',
      'import/export': 'error',
      'import/extensions': 'off',
      'import/no-unresolved': 'off',
      'import/no-extraneous-dependencies': 'off',
      'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
  }
}
