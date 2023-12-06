<template>
  <footer class="app-footer">
    <div class="container app-footer-container">
      <ul class="app-footer__navigation">
        <a
          href="https://fenixedu.org"
          class="app-footer__list-item"
        >
          Powered by FenixEdu&trade;
        </a>
      </ul>
      <p class="app-footer__copyright">
        {{ getCopyrightYear(2020) }} © <a href="https://tecnico.ulisboa.pt">Instituto Superior Técnico</a>
      </p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'BottomBar',
  methods: {
    getCopyrightYear (startYear) {
      if (new Date().getFullYear() > startYear) {
        return `${startYear}-${new Date().getFullYear()}`
      } else {
        return startYear
      }
    }
  }
}
</script>

<style lang="scss">
.app-footer {
  background-color: white;
  box-shadow: 0 -0.0625rem 0 0 rgba($dark, 0.1);
  font-size: 0.875rem;
  padding-top: 1rem;
  padding-bottom: 1rem;
}
.app-footer-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  @media screen and (max-width: 750px) {
    flex-flow: column nowrap;
    align-items: center;
  }
  // @media screen and (max-width: 10)
}
.app-footer__navigation {
  display: flex;
  justify-content: flex-start;
  color: $dark;
  @media screen and (max-width: 750px) {
    flex-flow: column nowrap;
  }
  .app-footer__list-item:first-child {
    padding-left: 0;
  }
  .app-footer__list-item:last-child {
    padding-right: 0;
  }
}

.app-footer__copyright {
  padding: 1rem 0;
  font-size: 0.875rem;
}
.app-footer__list-item {
  padding: 1rem;
  font-size: 0.875rem;
  @media screen and (max-width: 750px) {
    padding: 1rem 0;
  }
}

</style>
