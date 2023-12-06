<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 54 47"
    width="54px"
    class="alert"
  >
    <g
      fill="none"
      fill-rule="evenodd"
    >
      <path
        vector-effect="non-scaling-stroke"
        stroke="currentColor"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M28.924186 2.94447905L52.2489678 42.9934534c.5559003.9544886.2327807 2.1789008-.7217079 2.7348012-.30559.1779775-.6529068.2717454-1.0065467.2717454H3.50669989c-1.1045695 0-2-.8954305-2-2 0-.3582325.09621766-.7098897.27859758-1.0182208L25.474529 2.93280484c.5623478-.9507043 1.7889189-1.26552942 2.7396233-.70318157.2938045.17378733.5382389.41988171.7100337.71485578z"
      />
      <g
        transform="translate(25 19)"
        class="exclamation-mark"
      >
        <circle
          vector-effect="non-scaling-stroke"
          cx="2"
          cy="16.5"
          r="1.5"
          fill="currentColor"
        />
        <path
          vector-effect="non-scaling-stroke"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-width="2"
          d="M2 12V0"
        />
      </g>
    </g>
  </svg>
</template>
<style lang="scss">
.alert .explosion {
  transform-origin: center;
  transform: scale(2);
  stroke-dasharray: 22px;
  stroke-dashoffset: 22px;
}

@keyframes explosion {
  0% {
    stroke-dashoffset: 22px;
  }
  25%,
  100% {
    stroke-dashoffset: -22px;
  }
}

.animated-icon--is-playing .alert .exclamation-mark {
  transform: translateX(25px) translateY(19px);
  // animation: attention-needed 2s linear infinite;
  transform-origin: 27px 30px;
}

@keyframes attention-needed {
  0% {
    transform: translateX(25px) translateY(19px) scaleY(1);
  }
  20% {
    transform: translateX(25px) translateY(19px) scaleY(0.75);
  }
  30% {
    transform: translateX(25px) translateY(15px) scaleY(1.1);
  }
  38% {
    transform: translateX(25px) translateY(19px) scaleY(0.9);
  }
  45% {
    transform: translateX(25px) translateY(17px) scaleY(1);
  }
  48% {
    transform: translateX(25px) translateY(19px) scaleY(0.95);
  }
  50% {
    transform: translateX(25px) translateY(18px) scaleY(1);
  }
}

.animated-icon--is-playing .alert .exclamation-mark path {
  animation: exclamation-mark 2s infinite;
  stroke-dasharray: 14px;
}

@keyframes exclamation-mark {
  0%,
  10% {
    stroke-dashoffset: 0;
  }
  30%,
  40% {
    stroke-dashoffset: 14px;
  }
  70%,
  100% {
    stroke-dashoffset: 28px;
  }
}

.animated-icon--is-playing .alert .exclamation-mark circle {
  animation: exclamation-mark-dot 2s cubic-bezier(0.25, 0.46, 0.45, 0.94) infinite;
  transform-origin: 2px 17px;
}

@keyframes exclamation-mark-dot {
  0%,
  20% {
    transform: scale(1);
  }
  40%,
  50% {
    transform: scale(0);
  }
  80%,
  100% {
    transform: scale(1);
  }
}
</style>
