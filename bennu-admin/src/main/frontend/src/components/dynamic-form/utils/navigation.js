export function getPageAndSectionFromHash (hash) {
  const regex = /^#section-(?<page>[-]?\d+)-(?<section>[-]?\d+)$/
  const match = hash?.match(regex)
  const { page, section } = match?.groups
  return { page, section }
}

export function getSectionAnchor (page, section) {
  return `section-${page}-${section}`
}

export function getSectionLocation (page, section, quiet = false) {
  return { hash: `#${getSectionAnchor(page, section)}`, params: { page, section, quiet } }
}

export const scrollOffset = {
  y: 16 * 3.5 + 32 // ($header-height + 2rem) FIXME: this should be controlled by css somehow :(
}

export const scrollBehavior = function (to, from, savedPosition) {
  if (!to.hash) {
    return { x: 0, y: 0 }
  }
  if (to.params.quiet) {
    return savedPosition
  }
  return {
    selector: to.hash,
    offset: to.meta.scrollOffset,
    behavior: 'smooth'
  }
}
