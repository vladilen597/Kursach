import React, { memo } from "react";
import { useState } from "react";
import { AiFillEye, AiOutlineEye } from "react-icons/ai";

import "./CustomInput.scss";

const CustomInput = memo(
  ({
    label,
    type,
    handleChange,
    value,
    helperText,
    helperTextFlag,
    className,
  }) => {
    const [showPassword, setShowPassword] = useState(false);

    const handlePasswordShow = (event) => {
      event.preventDefault();
      setShowPassword((prevState) => !prevState);
    };

    return (
      <div className={className}>
        <section className="custom-input-block">
          <input
            type={
              type === "password" ? (showPassword ? "text" : "password") : type
            }
            className="custom-input-field"
            name="name"
            autoComplete="off"
            placeholder=" "
            onChange={handleChange}
            value={value}
          />
          <label className="custom-input-label" htmlFor="name">
            {label}
          </label>
          {type === "password" ? (
            <button
              type="button"
              className="custom-input-password-eye-button"
              onClick={handlePasswordShow}
            >
              {showPassword ? (
                <AiFillEye className="password-eye-icon" />
              ) : (
                <AiOutlineEye className="password-eye-icon" />
              )}
            </button>
          ) : (
            ""
          )}
          {helperTextFlag === true ? (
            <p className="custom-input-helper-text">{helperText}</p>
          ) : (
            ""
          )}
        </section>
      </div>
    );
  }
);

export default CustomInput;
