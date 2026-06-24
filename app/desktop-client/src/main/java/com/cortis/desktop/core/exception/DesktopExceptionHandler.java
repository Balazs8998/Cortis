package com.cortis.desktop.core.exception;

import com.cortis.desktop.core.exception.ex.ApiException;
import com.cortis.desktop.core.exception.ex.NetworkException;
import com.cortis.desktop.core.exception.ex.UnexpectedException;
import com.cortis.desktop.translation.service.TranslationService;
import javafx.scene.control.Alert;


public class DesktopExceptionHandler {

    private final TranslationService translationService;

    public DesktopExceptionHandler(
            TranslationService translationService
    ) {
        this.translationService = translationService;
    }

    public void handle(Throwable throwable) {

        if (throwable instanceof ApiException apiException) {
            handleApiException(apiException);
            return;
        }

        if (throwable instanceof NetworkException) {
            showError(
                    translationService.translate(
                            "desktop.error.connection_failed", ""
                    )
            );
            return;
        }

   if (throwable instanceof UnexpectedException){
       showError(throwable.getMessage());
       return;
   }

        showError(
                translationService.translate(
                        "common.error.unexpected", ""
                )
        );
    }

    private void handleApiException(ApiException exception) {

        ErrorResponse response = exception.getErrorResponse();

        String message =
                translationService.translate(response.messageKey(), response.messageKey());

        showError(message);
    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(translationService.translate(message, message));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
}