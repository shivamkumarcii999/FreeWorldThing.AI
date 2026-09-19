/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef4ff', 100: '#dce7fe', 200: '#c1d4fd', 300: '#96b8fb',
          400: '#6492f7', 500: '#406cf2', 600: '#2b4de6', 700: '#233bd4',
          800: '#2233ab', 900: '#213087', 950: '#191f52'
        }
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif']
      }
    },
  },
  plugins: [],
}
