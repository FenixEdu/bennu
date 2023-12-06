export const sleep = (ms) => new Promise(resolve => {
  setTimeout(resolve, ms)
})

export const normalize = (str) => {
  // https://en.wikipedia.org/wiki/Combining_Diacritical_Marks
  const diacriticsRegex = /[\u0300-\u036f]/g
  return str.toLowerCase().normalize('NFD').replace(diacriticsRegex, '')
}
