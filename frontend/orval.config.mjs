export default {
  'documed-api': {
    input: {
      target: 'http://localhost:8080/v3/api-docs',
    },
    output: {
      target: './src/shared/api/generated/generated.ts',
      mode: 'tags-split',
      client: 'react-query',
      clean: true,
      prettier: true,
      override: {
        mutator: {
          path: './src/shared/api/axios-instance.ts',
          name: 'customInstance',
        },
        query: {
          useInfinite: false,
        },
      },
      hooks: {
        afterAllFilesWrite: 'prettier --write',
      },
    },
  },
};
