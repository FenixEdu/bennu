<template>
  <nav
    v-if="totalPages > 1"
    class="page-nav"
    :aria-label="$t('aria-label.nav')"
  >
    <ul
      class="page-nav__list"
      :class="{ 'page-nav__list--center': centered }"
    >
      <li v-if="currentPage > 1">
        <router-link
          class="page-nav__item page-nav__item--previous"
          :to="{ query: { ...$route.query, page: currentPage - 1 } }"
        >
          <span class="sr-only">
            {{ $t('aria-label.previous') }}
          </span>
          <span aria-hidden="true">
            {{ $t('previous') }}
          </span>
        </router-link>
      </li>
      <li v-if="pagesToShow.length > 0 && !pagesToShow.includes(1)">
        <router-link
          class="page-nav__item"
          :to="{ query: { ...$route.query, page: 1 } }"
        >
          <span class="sr-only">
            {{ $t('aria-label.page-number', { page: 1 }) }}
          </span>
          <span aria-hidden="true">
            1
          </span>
        </router-link>
      </li>
      <li
        v-if="pagesToShow[0] > 2"
        class="page-nav__ellipsis"
      >
        <span>...</span>
      </li>
      <li
        v-for="page in pagesToShow"
        :key="page"
      >
        <router-link
          class="page-nav__item"
          :class="{ 'page-nav__item--current-page': page == currentPage }"
          :aria-current="page === currentPage ? 'page' : undefined"
          :to="{ query: { ...$route.query, page } }"
        >
          <span class="sr-only">
            {{ $t('aria-label.page-number', { page }) }}
          </span>
          <span aria-hidden="true">
            {{ page }}
          </span>
        </router-link>
      </li>
      <li
        v-if="pagesToShow[pagesToShow.length - 1] < totalPages - 1"
        class="page-nav__ellipsis"
      >
        <span>...</span>
      </li>
      <li v-if="pagesToShow.length > 0 && !pagesToShow.includes(totalPages)">
        <router-link
          class="page-nav__item"
          :to="{ query: { ...$route.query, page: totalPages } }"
        >
          <span class="sr-only">
            {{ $t('aria-label.page-number', { page: totalPages }) }}
          </span>
          <span aria-hidden="true">
            {{ totalPages }}
          </span>
        </router-link>
      </li>
      <li v-if="currentPage < totalPages">
        <router-link
          class="page-nav__item page-nav__item--next"
          :to="{ query: { ...$route.query, page: currentPage + 1 } }"
        >
          <span class="sr-only">
            {{ $t('aria-label.next') }}
          </span>
          <span aria-hidden="true">
            {{ $t('next') }}
          </span>
        </router-link>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  props: {
    currentPage: {
      type: Number,
      default: 1
    },
    totalItems: {
      type: Number,
      default: 0
    },
    itemsPerPage: {
      type: Number,
      required: true
    },
    offset: {
      type: Number,
      default: 1
    },
    centered: {
      type: Boolean,
      default: false
    }
  },
  i18n: {
    messages: {
      pt: {
        'aria-label': {
          nav: 'Paginação',
          previous: 'Página anterior',
          next: 'Página seguinte',
          'page-number': 'Página {page}'
        },
        previous: 'Anterior',
        next: 'Seguinte'
      },
      en: {
        'aria-label': {
          nav: 'Pagination',
          previous: 'Previous page',
          next: 'Next page',
          'page-number': 'Page {page}'
        },
        previous: 'Previous',
        next: 'Next'
      }
    }
  },
  computed: {
    totalPages () {
      return Math.ceil(this.totalItems / this.itemsPerPage)
    },
    pagesToShow () {
      let from = this.currentPage - this.offset
      if (from < 1) {
        from = 1
      }
      let to = from + this.offset * 2
      if (to >= this.totalPages) {
        to = this.totalPages
      }
      const pagesArray = []
      for (let page = from; page <= to; page++) {
        pagesArray.push(page)
      }
      return pagesArray
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/assets/scss/variables";

.page-nav {
  width: 100%;
  margin-top: 3.5rem;
}
.page-nav__list {
  display: flex;
  justify-content: flex-start;
  margin: 0 -0.25rem;
}
.page-nav__list--center {
  justify-content: center;
}
.page-nav__item,
.page-nav__ellipsis {
  display: inline-block;
  outline: none;
  appearance: none;
  line-height: 1.2;
  font-weight: 400;
  padding: 0.5rem 1rem;
  margin: 0 0.25rem;
  font-size: 0.875rem;
}
.page-nav__item {
  border-width: 0.0625rem;
  border-style: solid;
  border-radius: 0.125rem;
  transition-property: all;
  @include mdTransition;

  background-color: transparent;
  border-color: $light-gray;
  color: $dark-400;

  &:hover,
  &:focus {
    background-color: $blue-600;
    border-color: $blue-600;
    color: white;
  }
}
.page-nav__item--previous::before {
  content: "\00ab ";
}
.page-nav__item--next::after {
  content: " \00bb";
}
.page-nav__item--current-page {
  background-color: $blue;
  border-color: $blue;
  color: white;
}
</style>
