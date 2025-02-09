import pluginJs from '@eslint/js';
import tsParser from '@typescript-eslint/parser';
import eslintConfigPrettier from 'eslint-config-prettier';
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended';
import pluginReact from 'eslint-plugin-react';
import globals from 'globals';
import tseslint from 'typescript-eslint';

/** @type {import('eslint').Linter.Config[]} */
export default [
  {
    files: ['**/*.{js,mjs,cjs,ts,jsx,tsx}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 'latest',
        project: ['./tsconfig.app.json', './tsconfig.node.json'],
      },
      globals: { ...globals.browser, ...globals.node },
    },
    ignores: ['./dist/*', './node_modules/*'],
  },
  pluginJs.configs.recommended,
  ...tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  {
    rules: {
      'import/prefer-default-export': 'off',
      'react/react-in-jsx-scope': 'off',
      'react/function-component-definition': 'off',
      'import/extensions': 'off',
      'import/no-extraneous-dependencies': 'off',
      '@typescript-eslint/no-var-requires': 'off',
      'react-hooks/exhaustive-deps': 'off',
      'react/jsx-props-no-spreading': 'off',
      'react/require-default-props': 'off',
      '@typescript-eslint/no-unused-vars': 'warn',
      '@typescript-eslint/no-explicit-any': 'warn',
      'react/self-closing-comp': 'warn',
      '@typescript-eslint/no-throw-literal': 'off',
      '@typescript-eslint/return-await': 'warn',
      'no-restricted-syntax': 'off',
      'no-continue': 'off',
      'react/button-has-type': 'off',
      'react/prop-types': 'off',
      'react/no-array-index-key': 'off',
    },
  },
  eslintConfigPrettier,
  eslintPluginPrettierRecommended,
];
